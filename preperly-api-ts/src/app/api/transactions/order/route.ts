import { NextResponse } from "next/server";
import client from "@/lib/redisDb";
import Order from "@/models/order";
import mongoose from "mongoose";
import { connectToDatabase } from "@/utils/db";

interface ItemType {
  productId: string;
  dishName: string;
  category: string;
  containsDairy: boolean;
  quantity: number;
  price: number;
  imageUrl: string;
  itemType: string;
}

interface RequestData {
  vendorId: string;
  customerId: string;
  arrivalTime: Date;
  orderType: string;
  items: ItemType[];
}

export async function POST(request: Request): Promise<NextResponse> {
  try {
    const data: RequestData = await request.json();

    if (!data) {
      return NextResponse.json(
        { sucess: false, message: "Data is missing!" },
        { status: 404 }
      );
    }

    const totalAmount = data.items.reduce(
      (acc, item) => acc + item.price * item.quantity,
      0
    );

    if (!data.vendorId) {
      return NextResponse.json(
        { sucess: false, message: "VendorId is missing!" },
        { status: 404 }
      );
    }

    if (!data.customerId) {
      return NextResponse.json(
        { sucess: false, message: "CustomerId is missing!" },
        { status: 404 }
      );
    }

    if (!data.items || data.items.length === 0) {
      return NextResponse.json(
        { sucess: false, message: "Items are missing!" },
        { status: 404 }
      );
    }

    await client.setEx(
      `${data.customerId}-${data.vendorId}-${Date.now()}-order`,
      300,
      JSON.stringify(data)
    );

    console.log("Order redis successfully!");

    await connectToDatabase();

    const order = await Order.create({
      vendorId: data.vendorId,
      customerId: data.customerId,
      items: data.items.map((item) => ({
        productId: new mongoose.Types.ObjectId(item.productId),
        quantity: item.quantity,
        dishName: item.dishName,
        category: item.category,
        containsDairy: item.containsDairy,
        price: item.price,
        imageUrl: item.imageUrl,
        itemType: item.itemType,
      })),
      totalAmount: totalAmount,
      orderDate: new Date(),
      arrivalTime: data.arrivalTime,
      orderType: data.orderType,
      isActive: true,
    });

    console.log("Order saved successfully!");

    await order.save();

    console.log("Order saved successfully in mongodb!");

    return NextResponse.json(
      { sucess: true, message: "Order placed successfully!", order },
      { status: 200 }
    );
  } catch (error) {
    console.error("There was an error while ordering. We are sorry!", error);
    return NextResponse.json(
      { sucess: false, message: "An error occurred!" },
      { status: 500 }
    );
  }
}
