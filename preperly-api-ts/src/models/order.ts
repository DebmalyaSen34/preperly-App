import mongoose, { Schema, Document } from "mongoose";

interface OrderItem {
  productId: mongoose.Schema.Types.ObjectId;
  quantity: number;
  dishName: string;
  category: string;
  containsDairy: boolean;
  price: number;
  imageUrl: string;
  itemType: string;
}

interface OrderDocument extends Document {
  vendorId: mongoose.Schema.Types.ObjectId;
  customerId: mongoose.Schema.Types.ObjectId;
  items: OrderItem[];
  totalAmount: number;
  orderDate: Date;
  arrivalTime: Date;
  orderType: string;
  isActive: boolean;
}

const OrderItemSchema = new Schema({
  productId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Vendor.menu._id",
    required: true,
  },
  quantity: {
    type: Number,
    required: true,
  },
  dishName: {
    type: String,
    required: true,
  },
  category: {
    type: String,
    required: true,
  },
  containsDairy: {
    type: Boolean,
    required: true,
  },
  price: {
    type: Number,
    required: true,
  },
  imageUrl: {
    type: String,
    required: true,
  },
  itemType: {
    type: String,
    required: true,
  },
});

const OrderSchema = new Schema<OrderDocument>(
  {
    customerId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Customer",
      required: true,
      index: true,
    },
    orderDate: {
      type: Date,
      default: Date.now,
      required: true,
    },
    vendorId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Vendor",
      required: true,
      index: true,
    },
    items: [OrderItemSchema],
    totalAmount: {
      type: Number,
      required: true,
      default: 0,
    },
    arrivalTime: {
      type: Date,
      required: true,
    },
    orderType: {
      type: String,
      required: true,
      enum: ["takeaway", "dine-in"],
    },
    isActive: {
      type: Boolean,
      required: true,
      default: true,
    },
  },
  {
    timestamps: true,
  }
);

const Order =
  mongoose.models.Order || mongoose.model<OrderDocument>("Order", OrderSchema);

export default Order;
