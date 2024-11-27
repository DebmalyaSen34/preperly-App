import { NextResponse } from "next/server";
import connectToDatabase from "@/utils/db";
import OTP from "@/models/otp";

export async function POST(request) {

    // Parsing the data from body
    const { mobileNumber, userOtp } = await request.json();

    // Check if the phone number and OTP are provided
    if(!mobileNumber || !userOtp){
        return NextResponse.json({ message: "Phone number and OTP are required" }, { status: 400 });
    }

    // Validate input
    if (!/^\d{10}$/.test(mobileNumber)) {
        return NextResponse.json({ message: "Invalid phone number format" }, { status: 400 });
    }
    if (!/^\d{6}$/.test(userOtp)) {
        return NextResponse.json({ message: "Invalid OTP format" }, { status: 400 });
    }

    try {
        await connectToDatabase();

        // Fetch the OTP record
        const record = await OTP.findOne({ phoneNumber: mobileNumber });

        if (!record) {
            return NextResponse.json({ message: "Invalid OTP" }, { status: 401 });
        }

        const isExpired = new Date() - new Date(record.createdAt) > 5 * 60 * 1000; // 5 minutes
        if (isExpired) {
            await OTP.deleteOne({ phoneNumber: mobileNumber }); // Clean up expired OTP
            return NextResponse.json({ message: "OTP expired" }, { status: 410 });
        }

        if (record.otp === userOtp) {
            // OTP verified successfully
            await OTP.deleteOne({ phoneNumber: mobileNumber }); // Remove OTP after successful verification
            return NextResponse.json({ message: "OTP verified successfully" }, { status: 200 });
        } else {
            // Increment failed attempts
            await OTP.updateOne({ phoneNumber: mobileNumber }, { $inc: { attempts: 1 } });
            return NextResponse.json({ message: "Invalid OTP" }, { status: 401 });
        }
    } catch (error) {
        console.error("Error verifying OTP!", error);
        return NextResponse.json({ message: "Error occurred during OTP verification" }, { status: 500 });
    }
}
