import { NextResponse } from "next/server";
import { Client } from "pg";
import bcrypt from "bcrypt";
import redisClient from "@/lib/redisDb";
import otpGenerator from "otp-generator";

interface userType {
  fullName: string;
  email: string;
  password: string;
  phoneNumber: string;
}

export async function POST(request: Request): Promise<NextResponse> {
  try {
    const data: userType = await request.json();

    // Check if data is provided
    if (!data) {
      return NextResponse.json(
        { success: false, message: "Data not provided" },
        { status: 400 }
      );
    }

    const client = new Client(process.env.DATABASE_URL);
    await client.connect();

    // Check if user already exists
    const user = await client.query(
      "SELECT * FROM customers WHERE phonenumber = $1",
      [data.phoneNumber]
    );

    if (user.rows.length > 0) {
      return NextResponse.json(
        { success: false, message: "User already exists" },
        { status: 400 }
      );
    }

    await client.end();

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

    // Fast2SMS API key and URL from environment variables
    const api_key = process.env.FAST2SMS_API_KEY;
    const api_url = process.env.FAST2SMS_API_URL;

    if (!api_key || !api_url) {
      console.error("Fast2SMS API key or URL not provided");
      return NextResponse.json(
        { success: false, error: "Fast2SMS API key or URL not provided" },
        { status: 500 }
      );
    }

    // Headers for the Fast2SMS API request
    const headers = {
      authorization: api_key,
      "Content-Type": "application/json",
    };

    // Body for the Fast2SMS API request
    const body = JSON.stringify({
      authorization: api_key,
      route: "otp",
      sender_id: "TXTIND", // Sender ID, can be customized from Fast2SMS dashboard
      variables_values: otp, // The OTP to be sent
      language: "english",
      numbers: data.phoneNumber, // The phone number where the OTP should be sent
    });

    try {
      // Send the OTP using the Fast2SMS API
      const response = await fetch(api_url, {
        method: "POST",
        headers,
        body,
      });

      console.log("Raw response: ", response);

      // Parse the response from the Fast2SMS API
      const data = await response.json();

      console.log("Parsed response data: ", data);

      // Check if the OTP was sent successfully
      if (response.ok) {
        console.log("OTP sent successfully:", data);

        // store otp in redis
        await redisClient.setEx(`otp_${data.phoneNumber}`, 300, otp);

        console.log("OTP stored in redis successfully", otp);
      } else {
        console.error("Failed to send OTP:", data);
        return NextResponse.json(
          { error: "Failed to send OTP", data },
          { status: 500 }
        );
      }
    } catch (err) {
      console.error("Error sending OTP:", err);
      return NextResponse.json(
        { success: false, error: "Error sending OTP", err },
        { status: 500 }
      );
    }

    return NextResponse.json(
      {
        success: true,
        message: "OTP successfully sent to your mobile number!",
      },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error in user registration: ", error);
    return NextResponse.json(
      { success: false, message: "Internal server error" },
      { status: 500 }
    );
  }
}
