import { NextResponse } from "next/server";
import OTP from "@/models/otp";
import connectToDatabase from "@/utils/db";
import otpGenerator from "otp-generator";

// Function to handle OTP generation and sending
export async function POST(request){
    
    let phoneNumber;
    
    // Parse the phone number from the request body
    try {
        const body = await request.json();
        console.log(body);
        phoneNumber = body.phoneNumber;
    } catch (error) {
        return NextResponse.json({ message: "Invalid request body" }, {status: 400});
    }

    // Check if the phone number is provided
    if(!phoneNumber){
        return NextResponse.json({ message: "Phone number is required" }), {status: 400};
    }

    // Generate a 6-digit OTP
    const otp = otpGenerator.generate(6, {
        upperCaseAlphabets: false,
        lowerCaseAlphabets: false,
        digits: true,
        specialChars: false,
    });

    // Retrieve Fast2SMS API key and URL from environment variables
    const api_key = process.env.FAST2SMS_API_KEY;
    const api_url = process.env.FAST2SMS_API_URL;

    // Set up the headers for the API request
    const headers = {
        "authorization": api_key,
        "Content-Type": "application/json"
    };

    // Create the body for the API request
    const body = JSON.stringify({
        authorization: api_key,
        route: "otp",
        sender_id: "TXTIND",  // Sender ID, can be customized from Fast2SMS dashboard
        variables_values: otp, // The OTP to be sent
        language: "english",
        numbers: phoneNumber // The phone number where the OTP should be sent
    });

    try {
        // Send OTP via Fast2SMS API
        const response = await fetch(api_url, {
            method: "POST",
            headers,
            body
        });

        // Parse the response from the API
        const data = await response.json();

        // Check if the response is successful
        if(response.ok){
            // Connect to the database
            await connectToDatabase();

            // Save the OTP and phone number to the database
            await OTP.create({
                phoneNumber,
                otp,
            });

            // Return a success response
            return NextResponse.json({ message: "OTP sent successfully" }, {status: 200});
        }else{
            // Resturn an error response if the API call failed
            return NextResponse.error({ message: data.message });
        }
    } catch (error) {
        // Log in the error and return an error response
        console.error('Error sending OTP!', error);
        return NextResponse.json({ message: "An error occurred while sending OTP" }, {status: 500});
    }
}