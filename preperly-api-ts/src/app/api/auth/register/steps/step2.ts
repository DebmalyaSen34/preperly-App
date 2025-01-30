import { NextResponse } from "next/server";
import client from "@/lib/redisDb";
import { step2DataType } from "@/types/registration";

export default async function step2(
  step2Data: step2DataType
): Promise<NextResponse> {
  try {
    if (!step2Data) {
      return NextResponse.json(
        { sucess: false, message: "Please fill in all the fields!" },
        { status: 400 }
      );
    }

    const { phoneNumber, timings } = step2Data;

    // Check if redis has this phoneNumber
    const user = await client.get(phoneNumber);

    if (!user) {
      return NextResponse.json(
        { success: false, message: "User not found!" },
        { status: 404 }
      );
    }

    const userData = JSON.parse(user);

    userData.timings = timings;

    await client.setEx(phoneNumber, 3600, JSON.stringify(userData));

    return NextResponse.json(
      { success: true, message: "Step 2 data saved successfully!" },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error in step 2:", error);
    return NextResponse.json(
      { success: false, message: "Internal server error" },
      { status: 500 }
    );
  }
}
