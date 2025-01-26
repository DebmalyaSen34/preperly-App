import { NextResponse } from "next/server";
import User from "@/models/user";
import connectToDatabase from "@/utils/db";
import bcrypt from "bcrypt";

export async function POST(request){
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
        } = await request.json();

        if(!restaurantName || !restaurantAddress || !phoneNumber || !email || !password || !ownerName || !ownerPhoneNumber || !ownerEmail){
            return NextResponse.json({message: "Please fill in all the fields!"}, {status: 400});
        }

        const hashedPassword = bcrypt.hashSync(password, 10);

        await connectToDatabase();

        const existingUser = await User.findOne({phoneNumber});

        if(existingUser){
            return NextResponse.json({message: "User already exists!"}, {status: 400});
        }

        const user = new User({
            restaurantName,
            restaurantAddress,
            phoneNumber,
            alternateNumber,
            email,
            password: hashedPassword,
            ownerName,
            ownerPhoneNumber,
            ownerEmail,
            receiveUpdatesOnWhatsApp
        });

        await user.save();

        return NextResponse.json({message: "User is successfully registered!"}, {status: 200});
    } catch (error) {
        return NextResponse.json({message: "There was en error while registering you!"}, {status: 500});
        
    }
}