import mongoose from "mongoose";

// By extending mongoose. Document, the IOtp interface inherits all the properties and methods of a Mongoose document, ensuring that any object adhering to this interface can be used seamlessly with Mongoose's document methods and functionalities. This interface helps in maintaining type safety and consistency across the application when working with OTP documents.

export interface IOtp extends mongoose.Document {
    otp: string,
    phoneNumber: string,
    expiry?: Date,
    createdAt?: Date,
    attempts?: number
}

const otpSchema = new mongoose.Schema<IOtp>({
    phoneNumber: {
        type: String,
        required: true,
        unique: true
    },
    otp: {
        type: String,
        required: true
    },
    expiry: {
        type: Date,
        default: Date.now(),
        index: { expires: '5m' },
        required: true
    },
    createdAt: {
        type: Date,
        default: Date.now()
    },
    attempts: {
        type: Number,
        default: 0
    }
});

const OTP = mongoose.model<IOtp>("OTP", otpSchema);

export default OTP;