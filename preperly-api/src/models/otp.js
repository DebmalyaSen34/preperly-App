import mongoose from "mongoose";

const otpSchema = new mongoose.Schema({
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
        default: Date.now,
        index: {expires: '2m'},
        required: true 
    }
});

const OTP = mongoose.models.vendorOtp || mongoose.model('vendorOtp', otpSchema);

export default OTP;