import { NextResponse } from "next/server";
import otpGenerator from "otp-generator";
import client from "@/lib/redisDb";

interface RequestBody {
    mobileNumber: string
}

export async function POST(request:Request):Promise<NextResponse> {
    try {
        const { mobileNumber} : RequestBody = await request.json();

        if(!mobileNumber){
            return NextResponse.json({ message: "Please fill in all the fields!" }, { status: 400 });
        }

        const otp = otpGenerator.generate(
            6,
            {
                upperCaseAlphabets: false,
                lowerCaseAlphabets: false,
                digits: true,
                specialChars: false
            }
        );

        const api_key = process.env.FAST2SMS_API_KEY;
        const api_url = process.env.FAST2SMS_API_URL;

        if(!api_key || !api_url){
            return NextResponse.json({ message: "API key or URL not found" }, { status: 500 });
        }

        const headers = {
            "authorization": api_key,
            "Content-Type": "application/json"
        }

        const body = JSON.stringify({
            authorization: api_key,
            route: "otp",
            sender_id: "TXTIND",  // Sender ID, can be customized from Fast2SMS dashboard
            variables_values: otp, // The OTP to be sent
            language: "english",
            numbers: mobileNumber
        });

        try {
            // Send the OTP using the Fast2SMS API
            const response = await fetch(api_url, {
                method: "POST",
                headers,
                body
            });


            // Parse the response from the Fast2SMS API
            const data = await response.json();


            // Check if the OTP was sent successfully
            if (response.ok) {

                // Store the OTP in Redis with the mobile number as the key
                await client.setEx(mobileNumber, 300, otp);

                return NextResponse.json({ success: true, message: 'OTP sent successfully', data });
            } else {
                console.error('Failed to send OTP:', data);
                return NextResponse.json({ success:false, error: 'Failed to send OTP', data }, { status: 500 });
            }
        } catch (error) {
            // Handle any errors that occurred during the OTP sending process
            console.error('Error sending OTP:', error);
            return NextResponse.json({ success: false, error: 'Error sending OTP', sendOtp: otp }, { status: 500 });
        }

    } catch (error) {
        console.error("Error sending OTP:", error);
        return NextResponse.json({ message: "Internal server error" }, { status: 500 });
    }
}