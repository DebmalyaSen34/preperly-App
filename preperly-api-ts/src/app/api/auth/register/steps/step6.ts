import { NextResponse } from "next/server";
import client from "@/lib/redisDb";
import { connectToDatabase } from "@/utils/db";
import Vendor from "@/models/vendor";

export default async function step6(
  phoneNumber: string
): Promise<NextResponse> {
  try {
    if (!phoneNumber || phoneNumber === "") {
      return NextResponse.json(
        { success: false, message: "Phone number not provided!" },
        { status: 400 }
      );
    }

    const user = await client.get(phoneNumber);

    if (!user) {
      return NextResponse.json(
        { success: false, message: "User not found!" },
        { status: 404 }
      );
    }

    const userData = JSON.parse(user);

    console.log("====================================");
    console.log("userData: ", userData);
    console.log("====================================");

    await connectToDatabase();

    const vendor = new Vendor({
      restaurantName: userData.restaurantName,
      restaurantAddress: userData.restaurantAddress,
      phoneNumber: userData.phoneNumber,
      alternateNumber: userData.alternateNumber,
      password: userData.password,
      email: userData.email,
      ownerName: userData.ownerName,
      ownerPhoneNumber: userData.ownerPhoneNumber,
      ownerEmail: userData.ownerEmail,
      receiveUpdatesOnWhatsApp: userData.receiveUpdatesOnWhatsApp,
      timings: userData.timings,
      fssai: userData.fssai,
      gstin: userData.gstin,
      pan: userData.pan,
      bankAccount: userData.bankAccount,
      imageUrls: userData.restaurantImagesUrl,
      logoUrl: userData.restaurantLogoUrl,
      menu: userData.menuItems,
    });

    await vendor.save();

    await client.del(phoneNumber);

    return NextResponse.json(
      { success: true, message: "User registered successfully!" },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error in step 6:", error);
    return NextResponse.json(
      { success: false, message: "Internal server error in step 6" },
      { status: 500 }
    );
  }
}
