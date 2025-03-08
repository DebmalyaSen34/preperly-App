//! Fix step back eror
import { NextResponse } from "next/server";
import bcrypt from "bcrypt";
import client from "@/lib/redisDb";
import { step1DataType } from "@/types/registration";

export default async function step1(
  step1Data: step1DataType
): Promise<NextResponse> {
  try {
    const {
      restaurantName,
      restaurantAddress,
      phoneNumber,
      email,
      password,
      ownerName,
      ownerPhoneNumber,
      ownerEmail,
    }: step1DataType = step1Data;

    if (
      !restaurantName ||
      !restaurantAddress ||
      !phoneNumber ||
      !email ||
      !password ||
      !ownerName ||
      !ownerPhoneNumber ||
      !ownerEmail
    ) {
      return NextResponse.json(
        { message: "Please fill in all the fields!" },
        { status: 400 }
      );
    }

    const hashedPassword = bcrypt.hashSync(password, 10);

    if (await client.get(phoneNumber)) {
      return NextResponse.json(
        { message: "User already exists!" },
        { status: 400 }
      );
    }

    // Save the data to redis for now
    await client.setEx(
      phoneNumber,
      3600,
      JSON.stringify({ ...step1Data, password: hashedPassword })
    );

    return NextResponse.json(
      { success: true, message: "Step 1 data saved successfully!" },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error in step 1:", error);
    return NextResponse.json(
      { success: false, message: "Internal server error" },
      { status: 500 }
    );
  }
}
