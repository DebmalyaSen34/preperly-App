import { NextResponse } from "next/server";
import client from "@/lib/redisDb";
import { Client as cockraochClient } from "pg";
import { userType } from "@/types/userRegistration";

async function insertUser(userData: userType): Promise<boolean> {
  let clientOfCockroach: cockraochClient | null = null;

  try {
    clientOfCockroach = new cockraochClient(process.env.COCKROACH_DATABASE_URL);
    await clientOfCockroach.connect();

    const query = `
      INSERT INTO customers
      (
      name,
      phonenumber,
      email,
      password
      )
      VALUES ($1, $2, $3, $4)
    `;

    const values = [
      userData.fullName,
      userData.phoneNumber,
      userData.email,
      userData.password,
    ];

    const result = await clientOfCockroach.query(query, values);

    if (result.rowCount === null) return false;

    return result.rowCount > 0;
  } catch (error) {
    console.error("Error inserting user into CockroachDB:", error);
    return false;
  } finally {
    if (clientOfCockroach) {
      await clientOfCockroach.end();
    }
  }
}

async function verifyOtp(
  phoneNumber: string,
  userOtp: string
): Promise<boolean> {
  try {
    const expectedOtp = await client.get(`otp_${phoneNumber}`);

    return expectedOtp !== null && userOtp === expectedOtp;
  } catch (error) {
    console.error("Error verifying OTP:", error);
    return false;
  }
}

export async function OPTIONS(): Promise<NextResponse> {
  return new NextResponse(null, {
    status: 204,
    headers: {
      "Access-Control-Allow-Origin": "*", // Replace with specific origins in production
      "Access-Control-Allow-Methods": "POST, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type, Authorization",
    },
  });
}

export async function POST(request: Request): Promise<NextResponse> {
  try {
    const url = new URL(request.url);
    const queryParameters = url.searchParams;

    const { phoneNumber } = Object.fromEntries(queryParameters.entries());

    if (!phoneNumber) {
      return NextResponse.json(
        { success: false, message: "Phone number not provided!" },
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

    const { userOtp } = await request.json();

    if (!userOtp) {
      return NextResponse.json(
        { success: false, message: "OTP not provided" },
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

    if (!(await verifyOtp(phoneNumber, userOtp))) {
      return NextResponse.json(
        { success: false, message: "Invalid OTP" },
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

    const userDataString = await client.get(`user_${phoneNumber}`);
    if (!userDataString) {
      return NextResponse.json(
        { success: false, message: "User data not found!" },
        {
          status: 404,
          headers: {
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Methods": "POST, OPTIONS",
            "Access-Control-Allow-Headers": "Content-Type, Authorization",
          },
        }
      );
    }
    const userData: userType = JSON.parse(userDataString);

    const registrationSuccess = await insertUser(userData);

    if (registrationSuccess) {
      await client.del(`user_${phoneNumber}`);
      await client.del(`otp_${phoneNumber}`);

      return NextResponse.json(
        { success: true, message: "User registered successfully!" },
        {
          status: 200,
          headers: {
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Methods": "POST, OPTIONS",
            "Access-Control-Allow-Headers": "Content-Type, Authorization",
          },
        }
      );
    }

    return NextResponse.json(
      {
        success: false,
        message: "User registration failed! Please try again.",
      },
      {
        status: 500,
        headers: {
          "Access-Control-Allow-Origin": "*",
          "Access-Control-Allow-Methods": "POST, OPTIONS",
          "Access-Control-Allow-Headers": "Content-Type, Authorization",
        },
      }
    );
  } catch (error) {
    console.error("Error registering user:", error);
    return NextResponse.json(
      { success: false, message: "Internal server error" },
      {
        status: 500,
        headers: {
          "Access-Control-Allow-Origin": "*",
          "Access-Control-Allow-Methods": "POST, OPTIONS",
          "Access-Control-Allow-Headers": "Content-Type, Authorization",
        },
      }
    );
  }
}
