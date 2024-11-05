import mongoose from "mongoose";

const userSchema = new mongoose.Schema({
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
        required: true, unique: true 
    },
    ownerEmail: { 
        type: String, 
        unique: true 
    },
    receiveUpdatesOnWhatsApp: { 
        type: Boolean 
    }
});

const User = mongoose.models.User || mongoose.model('User', userSchema);

export default User;