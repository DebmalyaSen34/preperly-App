import mongoose from "mongoose";

interface User extends mongoose.Document {
    restaurantName: string,
    restaurantAddress: string,
    phoneNumber: string,
    alternateNumber?: string,
    email: string,
    password: string,
    ownerName: string,
    ownerPhoneNumber: string,
    ownerEmail: string,
    recieveUpdatesOnWhatsApp?: boolean
}

const userSchema = new mongoose.Schema<User>({
    restaurantName: {
        type: String,
        required: true
    },
    restaurantAddress: {
        type: String,
        required: true
    },
    phoneNumber: {
        type: String,
        required: true,
        unique: true
    },
    alternateNumber: {
        type: String,
        required: false,
        unique: true
    },
    email: {
        type: String,
        required: true,
        unique: true
    },
    password: {
        type: String,
        required: true
    },
    ownerName: {
        type: String,
        required: true
    },
    ownerPhoneNumber: {
        type: String,
        required: true
    },
    ownerEmail: {
        type: String,
        required: true,
        unique: true
    },
    recieveUpdatesOnWhatsApp: {
        type: Boolean,
        required: false
    }
});

const Vendor = mongoose.model<User>("vendor", userSchema);

export default Vendor;