import mongoose from "mongoose";

interface itemType {
    productId: mongoose.Schema.Types.ObjectId,
    price: number,
    quantity: number,
    dishName: string,
    imageUrl: string
};

interface orderType extends mongoose.Document {
    customerId: mongoose.Schema.Types.ObjectId,
    orderDate: Date,
    restaurantId: mongoose.Schema.Types.ObjectId,
    items: itemType[],
    totalAmount: number,
    arrivalTime: Date,
    orderType: string,
    isActive: boolean,
};

const orderSchema = new mongoose.Schema({
    customerId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "debs",
        required: true,
        index: true
    },
    orderDate: {
        type: Date,
        default: Date.now(),
        required: true,
    },
    restaurantId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "vendor",
        required: true,
        index: true
    },
    items: [{
        productId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: "Product",
            required: true
        },
        price: {
            type: Number,
            required: true,
            min: 0
        },
        quantity: {
            type: Number,
            required: true,
            min: 1
        },
        dishName: {
            type: String,
            required: true
        },
        imageUrl: {
            type: String,
            required: true
        }
    }],
    totalAmount: {
        type: Number,
        required: true,
        default: 0
    },
    arrivalTime: {
        type: Date,
        required: true
    },
    orderType: {
        type: String,
        required: true,
        enum: ["takeaway", "dine-in"]
    },
    isActive: {
        type: Boolean,
        required: true,
        default: true
    }
}, {
    timestamps: true
});

const Order = mongoose.model<orderType>("Order", orderSchema);

export default Order;