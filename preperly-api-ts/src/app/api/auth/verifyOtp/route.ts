import { NextResponse } from "next/server";
import client from "@/lib/redisDb";

interface RequestBody {
    mobileNumber: string,
    otp: string
}

export async function POST(request:Request) : Promise<NextResponse> {
    try {
        const { mobileNumber, otp } : RequestBody = await request.json();

        if(!mobileNumber || !otp){
            return NextResponse.json({ message: "Please fill in all the fields!" }, { status: 400 });
        }

        const existingOtp = await client.get(mobileNumber);

        if(!existingOtp){
            return NextResponse.json({ message: "No OTP found!" }, { status: 400 });
        }

        if(existingOtp !== otp){
            return NextResponse.json({ message: "Invalid OTP!" }, { status: 400 });
        }

        await client.del(mobileNumber);
        
        return NextResponse.json({ message: "OTP verified successfully!" }, { status: 200 });
    } catch (error) {
        console.error('Error verifying OTP:', error);
        return NextResponse.json({ message: "An error occurred while verifying OTP!" }, { status: 500 });
    }
}