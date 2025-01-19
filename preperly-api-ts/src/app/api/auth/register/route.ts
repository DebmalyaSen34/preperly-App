import { NextResponse } from "next/server";
import bcrypt from 'bcrypt';
import { connectToDatabase } from "@/utils/db";
import client from "@/lib/redisDb";
import Vendor from "@/models/user";
import { uploadDocument } from "@/lib/uploadDocuments";

interface step1DataType {
    restaurantName: string,
    restaurantAddress: string,
    phoneNumber: string,
    alternateNumber?: string,
    email: string,
    password: string,
    ownerName: string,
    ownerPhoneNumber: string,
    ownerEmail: string,
    receiveUpdatesOnWhatsApp?: boolean
}

interface timeSlot {
    openTime: string,
    closeTime: string
}

interface dateTimeDataType {
    day: 'monday' | 'tuesday' | 'wednesday' | 'thursday' | 'friday' | 'saturday' | 'sunday',
    slots: timeSlot[]
}

interface step2DataType {
    phoneNumber: string,
    timings: dateTimeDataType[]
}


//* Use redis to store the data and once all the data is stored, then save it to the database

// For each step different request body


//* Check if user exists
export async function userExists(mobileNumber: string): Promise<boolean> {
    try {

        await connectToDatabase();

        const user = await Vendor.findOne({ phoneNumber: mobileNumber });

        if (user) {
            return true;
        } else {
            return false;
        }
    } catch (error) {
        console.error("Error checking if user exists:", error);
        throw new Error;
    }
}


//* Step 3: Push restaurant documents
export async function step3(formData: FormData): Promise<NextResponse> {
    try {
        const fssaiLicense = formData.get('fssaiLicense') as string;
        const gstin = formData.get('gstin') as string;
        const panCard = formData.get('panCard') as string;
        const accountNumber = formData.get('accountNumber') as string;
        const accountHolderName = formData.get('accountHolderName') as string;
        const fssaiDocument = formData.get('fssaiDocument') as File;
        const gstinDocument = formData.get('gstinDocument') as File;
        const panCardDocument = formData.get('panCardDocument') as File;

        if (!fssaiLicense || !gstin || !panCard || !accountNumber || !accountHolderName ||
            !fssaiDocument || !gstinDocument || !panCardDocument) {
            return NextResponse.json({ success: false, message: "Please fill in all the fields!" }, { status: 400 });
        }

        console.log('Received Documents: ', { fssaiDocument, gstinDocument, panCardDocument });

        // Upload documents
        const fssaiUpload = await uploadDocument(fssaiDocument);
        const gstinUpload = await uploadDocument(gstinDocument);
        const panCardUpload = await uploadDocument(panCardDocument);

        if (!fssaiUpload.success || !gstinUpload.success || !panCardUpload.success) {
            return NextResponse.json({
                success: false,
                message: "Error uploading documents",
                errors: {
                    fssai: fssaiUpload.error,
                    gstin: gstinUpload.error,
                    panCard: panCardUpload.error
                }
            }, { status: 500 });
        }

        // Store document references in Redis (you may want to store these in your database later)
        await client.setEx(`${fssaiLicense}_fssaiDocument`, 3600, String(fssaiUpload.fileId));
        await client.setEx(`${gstin}_gstinDocument`, 3600, String(gstinUpload.fileId));
        await client.setEx(`${panCard}_panCardDocument`, 3600, String(panCardUpload.fileId));

        return NextResponse.json({ success: true, message: "Documents uploaded successfully!" }, { status: 200 });

    } catch (error) {
        console.error("Error in processing documents in step 3:", error);
        return NextResponse.json({ success: false, message: "Error uploading documents" }, { status: 500 });
    }
}

//* Step 2: Push restaurant Type and Timings
async function step2(step2Data: step2DataType): Promise<NextResponse> {
    try {
        if (!step2Data) {
            return NextResponse.json({ sucess: false, message: "Please fill in all the fields!" }, { status: 400 });
        }

        const { phoneNumber, timings } = step2Data;

        // Check if redis has this phoneNumber
        const user = await client.get(phoneNumber);

        if (!user) {
            return NextResponse.json({ success: false, message: "User not found!" }, { status: 404 });
        }

        await client.setEx(`${phoneNumber}_timings`, 3600, JSON.stringify(timings));

        return NextResponse.json({ success: true, message: "Step 2 data saved successfully!" }, { status: 200 });
    } catch (error) {
        console.error("Error in step 2:", error);
        return NextResponse.json({ success: false, message: "Internal server error" }, { status: 500 });

    }
}

//* Step 1: Push user information to redis
async function step1(step1Data: step1DataType): Promise<NextResponse> {
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

        if (!restaurantName || !restaurantAddress || !phoneNumber || !email || !password || !ownerName || !ownerPhoneNumber || !ownerEmail) {
            return NextResponse.json({ message: "Please fill in all the fields!" }, { status: 400 });
        }

        const hashedPassword = bcrypt.hashSync(password, 10);

        if (await userExists(phoneNumber)) {
            return NextResponse.json({ message: "User already exists!" }, { status: 400 });
        }

        // Save the data to redis for now
        await client.setEx(phoneNumber, 3600, JSON.stringify({ ...step1Data, password: hashedPassword }));

        return NextResponse.json({ success: true, message: "Step 1 data saved successfully!" }, { status: 200 });
    } catch (error) {
        console.error("Error in step 1:", error);
        return NextResponse.json({ success: false, message: "Internal server error" }, { status: 500 });
    }
}


export async function POST(request: Request): Promise<NextResponse> {
    try {
        const url = new URL(request.url);
        const queryParameters = url.searchParams;

        const { step } = Object.fromEntries(queryParameters.entries());
        console.log(typeof (step));

        switch (step) {
            case '1':
                const data: step1DataType = await request.json();
                return step1(data);
            case '2':
                const data2: step2DataType = await request.json();
                return step2(data2);
            case '3':
                const formData = await request.formData();
                return step3(formData);
        }

        return NextResponse.json({ success: true, message: "User registered successfully!" }, { status: 200 });
    } catch (error) {
        console.error("Error registering user:", error);
        return NextResponse.json({ success: false, message: "Internal server error" }, { status: 500 });
    }
}