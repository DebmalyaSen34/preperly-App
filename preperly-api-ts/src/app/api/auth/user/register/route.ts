import { NextResponse } from "next/server";
import bcrypt from "bcrypt";
import redisClient from "@/lib/redisDb";
import otpGenerator from "otp-generator";
import { userType } from "@/types/userRegistration";
import { supabase } from "@/lib/supbaseDb";
import { corsHeaders, withCORS } from "@/utils/cors";

async function checkUserExists(phoneNumber: string): Promise<boolean> {

  const { data, error } = await supabase
    .from("customers")
    .select("*")
    .eq("phoneNumber", phoneNumber);

  if (error) {
    console.error("Error checking user existence:", error);
    return false;
  }

  return data && data.length > 0;
}

async function POST(request: Request): Promise<NextResponse> {
  try {
    const data: userType = await request.json();

    // Check if data is provided
    if (!data) {
      return NextResponse.json(
        { success: false, message: "Data not provided" },
        {
          status: 400,
          headers: {
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Methods": "POST, OPTIONS",
            "Access-Control-Allow-Headers": "Content-Type, Authorization",
          },
        }
      );
    }

    if (await checkUserExists(data.phoneNumber)) {
      return NextResponse.json(
        { success: false, message: "User already exists" },
        {
          status: 400,
          headers: {
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Methods": "POST, OPTIONS",
            "Access-Control-Allow-Headers": "Content-Type, Authorization",
          },
        }
      );
    }

    // hash password
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(data.password, salt);

    data.password = hashedPassword;

    // store the user temporarily in redis
    await redisClient.setEx(
      `user_${data.phoneNumber}`,
      300,
      JSON.stringify(data)
    );

    //* send otp to user

    // generate otp
    const otp = otpGenerator.generate(6, {
      upperCaseAlphabets: false,
      lowerCaseAlphabets: false,
      digits: true,
      specialChars: false,
    });

    console.log("Otp generated: ", otp);

    /* Using another messagin service */
    try {
      // Use environment variables for sensitive data
      const messageServiceUrl = process.env.MESSAGE_CENTRAL_URL || "https://cpaas.messagecentral.com/verification/v3/send";
      const customerID = process.env.MESSAGE_CENTRAL_CUSTOMER_ID || "C-8CE892DD3F964C9";
      const authToken = process.env.MESSAGE_CENTRAL_AUTH_TOKEN;

      if (!authToken) {
        console.error("Message service auth token is not configured");
        return NextResponse.json(
          { success: false, message: "OTP service configuration error" },
          {
            status: 500,
            headers: corsHeaders,
          }
        );
      }

      // Build the request URL with the dynamic phone number
      const url = new URL(messageServiceUrl);
      url.searchParams.append("countryCode", "91");
      url.searchParams.append("customerId", customerID);
      url.searchParams.append("flowType", "SMS");
      url.searchParams.append("mobileNumber", data.phoneNumber);
      url.searchParams.append("otpLength", "6");
      url.searchParams.append("otpValue", otp); // Add the OTP to the request

      // Use modern fetch API with proper error handling
      const response = await fetch(url.toString(), {
        method: "POST",
        headers: {
          "authToken": authToken,
          "Content-Type": "application/json",
        }
      });

      const responseData = await response.json();

      if (!response.ok) {
        console.error("Failed to send OTP:", responseData);
        return NextResponse.json(
          { success: false, message: "Failed to send verification code" },
          {
            status: 500,
            headers: corsHeaders,
          }
        );
      }

      console.log("OTP sent successfully:", responseData);

      // Store OTP in Redis for verification
      await redisClient.setEx(`otp_${data.phoneNumber}`, 300, otp);
      console.log("OTP stored in Redis successfully");

    } catch (error) {
      console.error("Error sending OTP:", error);
      return NextResponse.json(
        { success: false, message: "Failed to send verification code" },
        {
          status: 500,
          headers: corsHeaders,
        }
      );
    }

    return NextResponse.json(
      {
        success: true,
        message: "OTP successfully sent to your mobile number!",
      },
      {
        status: 200,
        headers: corsHeaders,
      });
  } catch (error) {
    console.error("Error in user registration: ", error);
    return NextResponse.json(
      { success: false, message: "Internal server error" },
      {
        status: 500,
        headers: corsHeaders,
      }
    );
  }
}

export { POST };
export const OPTIONS = withCORS(POST);