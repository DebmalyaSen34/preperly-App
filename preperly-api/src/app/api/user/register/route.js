import { NextResponse } from "next/server";
import User from "@/models/user";
import connectToDatabase from "@/utils/db";

export async function GET(request){
    try {
        const {
            restaurantName,
            restaurantAddress,
            phoneNumber,
            alternateNumber,
            email,
            password,
            ownerName,
            ownerPhoneNumber,
            ownerEmail,
            receiveUpdatesOnWhatsApp
        } = request.body;

        connectToDatabase();
    } catch (error) {
        return NextResponse.json({message: "Error"}, {status: 500});
        
    }
    return NextResponse.json({message: "Hello World"}, {status: 200});
}