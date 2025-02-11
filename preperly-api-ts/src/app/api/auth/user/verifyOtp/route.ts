import { NextResponse } from "next/server";
import client from "@/lib/redisDb";
import { Client as cockraochClient } from "pg";
import { userType } from "@/types/userRegistration";

async function insertUser(userData: userType): Promise<boolean> {
  const client = new cockraochClient(process.env.COCKROACH_DATABASE_URL);
  await client.connect();

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

  const result = await client.query(query, values);

  if (result.rowCount === 0 || !result) {
    return false;
  }

  return true;
}

async function verifyOtp(
  phoneNumber: string,
  userOtp: string
): Promise<boolean> {
  const actualOtp = await client.get(`otp_${phoneNumber}`);

  if (!actualOtp) {
    return false;
  }

  if (userOtp !== actualOtp) {
    return false;
  }

  return true;
}

export async function POST(request: Request): Promise<NextResponse> {
  try {
    const url = new URL(request.url);
    const queryParameters = url.searchParams;

    const { phoneNumber } = Object.fromEntries(queryParameters.entries());

    if (!phoneNumber) {
      return NextResponse.json(
        { success: false, message: "Phone number not provided!" },
        { status: 400 }
      );
    }

    const { userOtp } = await request.json();

    if (!userOtp) {
      return NextResponse.json(
        { success: false, message: "OTP not provided" },
        { status: 400 }
      );
    }

    if (!(await verifyOtp(phoneNumber, userOtp))) {
      return NextResponse.json(
        { success: false, message: "Invalid OTP" },
        { status: 400 }
      );
    }

    const userDataString = await client.get(`user_${phoneNumber}`);
    if (!userDataString) {
      return NextResponse.json(
        { success: false, message: "User data not found!" },
        { status: 404 }
      );
    }
    const userData: userType = JSON.parse(userDataString);

    if (await insertUser(userData)) {
      return NextResponse.json(
        { success: true, message: "User registered successfully!" },
        { status: 200 }
      );
    }

    return NextResponse.json(
      {
        success: false,
        message: "User registration failed! Please try again.",
      },
      { status: 500 }
    );
  } catch (error) {
    console.error("Error registering user:", error);
    return NextResponse.json(
      { success: false, message: "Internal server error" },
      { status: 500 }
    );
  }
}
