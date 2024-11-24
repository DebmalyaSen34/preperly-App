import mongoose from "mongoose";

// Define the schema for the OTP model
const otpSchema = new mongoose.Schema({
    phoneNumber: { 
        type: String, 
        required: true, 
        unique: true // Ensure that each phone number has only one OTP record 
    },
    otp: { 
        type: String, 
        required: true // OTP value
    },
    expiry: { 
        type: Date, 
        default: Date.now, // Set the default value tom the current date and time
        index: {expires: '2m'}, // Automatically delete the record after 2 minutes
        required: true // Ensure that the expiry date is always set
    },
    createdAt: {
        type: Date,
        default: Date.now, 
    },
    attempts: {
        type: Number,
        default: 0 // Initialize the number of failed attempts to 0
    }
});

// Create the OTP model if it doesn't already exist
const OTP = mongoose.models.vendorOtp || mongoose.model('vendorOtp', otpSchema);

export default OTP;