import Vendor from "@/models/vendor";
import { connectToDatabase } from "./db";

/**
 * Retrieves the vendor ID associated with a phone number.
 * 
 * @param phoneNumber - The phone number to search for.
 * @returns A promise that resolves to the vendor ID.
 * @throws An error if the phone number is not provided, the vendor is not found, or there is an error getting the vendor ID.
 */
export async function getVendorIdbyPhoneNumber(phoneNumber: string): Promise<string> {

    if (!phoneNumber || phoneNumber.length === 0) {
        throw new Error("Phone number is required")
    }

    try {
        await connectToDatabase();
    } catch (error) {
        console.error("Error getting vendor id by phone number:", error);
        throw new Error("Error getting vendor id by phone number");
    }

    try {

        const vendor = await Vendor.findOne({ phoneNumber: phoneNumber }) as { _id: string };

        if (!vendor) {
            throw new Error("Vendor not found");
        }
        return vendor._id.toString();
    } catch (error) {
        console.error("Error getting vendor id by phone number:", error);
        throw new Error("Error getting vendor id by phone number");
    }
}