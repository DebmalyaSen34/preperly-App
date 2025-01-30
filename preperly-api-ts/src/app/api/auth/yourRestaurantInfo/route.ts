import { NextResponse } from "next/server";
import { connectToDatabase } from "@/utils/db";
import bcrypt from "bcrypt";
import Vendor from "@/models/vendor";

export async function POST(request: Request): Promise<NextResponse> {
  try {
    const credentials = await request.json();

    const { phoneNumber, password } = credentials;

    if (!phoneNumber || !password) {
      return NextResponse.json(
        { success: false, message: "Please provide phone number and password" },
        { status: 400 }
      );
    }

    await connectToDatabase();

    const vendor = await Vendor.findOne({ phoneNumber: phoneNumber });

    if (!vendor) {
      return NextResponse.json(
        { success: false, message: "User not found!" },
        { status: 404 }
      );
    }

    const isPasswordValid = await bcrypt.compare(password, vendor.password);

    if (!isPasswordValid) {
      return NextResponse.json(
        { success: false, message: "Invalid password!" },
        { status: 400 }
      );
    }

    console.log("====================================");

    console.log("vendor: ", vendor);

    console.log("====================================");

    return NextResponse.json(
      {
        success: true,
        message: "User authenticated successfully!",
        data: vendor,
      },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error authenticating user:", error);
    return NextResponse.json(
      { success: false, message: "Internal server error" },
      { status: 500 }
    );
  }
}
