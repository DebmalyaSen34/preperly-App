import { NextResponse } from "next/server";
import OTP from "@/models/otp";
import connectToDatabase from "@/utils/db";
import otpGenerator from "otp-generator";

// Function to handle OTP generation and sending
export async function POST(request) {

    try {
        // Extract the mobile number from the request body
        const { mobileNumber } = await request.json();

        if(!mobileNumber){
            return NextResponse.json({message: "Please provide a mobile number or make sure your body has mobileNumber field!"}, {status: 400});
        }



        // Generate a 6-digit OTP
        const otp = otpGenerator.generate(6, {
            upperCaseAlphabets: false,
            lowerCaseAlphabets: false,
            digits: true,
            specialChars: false,
        });

        // Fast2SMS API key and URL from environment variables
        const api_key = process.env.FAST2SMS_API_KEY;
        const api_url = process.env.FAST2SMS_API_URL;

        // Headers for the Fast2SMS API request
        const headers = {
            "authorization": api_key,
            "Content-Type": "application/json"
        };

        // Body for the Fast2SMS API request
        const body = JSON.stringify({
            authorization: api_key,
            route: "otp",
            sender_id: "TXTIND",  // Sender ID, can be customized from Fast2SMS dashboard
            variables_values: otp, // The OTP to be sent
            language: "english",
            numbers: mobileNumber // The phone number where the OTP should be sent
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

                await connectToDatabase();

                await OTP.create({
                    phoneNumber: mobileNumber,
                    otp: otp,
                    attempts: 0
                });

                return NextResponse.json({ success: true, message: 'OTP sent successfully', data });
            } else {
                console.error('Failed to send OTP:', data);
                return NextResponse.json({ success:false, error: 'Failed to send OTP', data }, { status: 500 });
            }
        } catch (error) {
            // Handle any errors that occurred during the OTP sending process
            console.error('Error sending OTP:', error);
            return NextResponse.json({ success: false, error: 'Error sending OTP' }, { status: 500 });
        }
    } catch (error) {
        console.error('Error generating OTP:', error);
        return NextResponse.json({ success: false, error: 'Error generating OTP' }, { status: 500 });
    }
}