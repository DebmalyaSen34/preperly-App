import mongoose from "mongoose";

export interface OrderItem {
  productId: mongoose.Schema.Types.ObjectId;
  dishName: string;
  category: string;
  containsDairy: boolean;
  quantity: number;
  price: number;
  imageUrl: string;
  itemType: string;
}
