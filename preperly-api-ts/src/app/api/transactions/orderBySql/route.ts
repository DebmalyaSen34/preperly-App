import { NextResponse } from "next/server";
import { Client } from "pg";
import * as dotenv from "dotenv";

dotenv.config({ path: ".env.local" });

interface RequestData {
  vendorId: string;
  customerId: string;
  arrivalTime: Date;
  orderType: string;
  items: JSON;
  orderStatus: string;
  totalAmount: number;
  totalQuantity: number;
}

export async function POST(request: Request): Promise<NextResponse> {
  try {
    const data: RequestData = await request.json();

    if (!data) {
      return NextResponse.json(
        { success: false, message: "Data is missing!" },
        { status: 404 }
      );
    }

    console.log("====================================");
    console.log("data: ", data);
    console.log("====================================");

    // Connect to CockroachDB
    const cockraochClient = new Client(process.env.COCKROACH_DATABASE_URL);
    await cockraochClient.connect();

    // Insert order data into CockroachDB
    const orderQuery = `
      INSERT INTO orders (vendor_id, customer_id, arrivaltime, ordertype, items, orderstatus, totalamount, totalquantity)
      VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
    `;

    const values = [
      data.vendorId,
      data.customerId,
      data.arrivalTime,
      data.orderType,
      data.items,
      data.orderStatus,
      data.totalAmount,
      data.totalQuantity,
    ];

    const result = await cockraochClient.query(orderQuery, values);

    // Check if data is inserted into CockroachDB
    if (result.rowCount === 0) {
      return NextResponse.json(
        { success: false, message: "Error in inserting data into CockroachDB" },
        { status: 500 }
      );
    }

    await cockraochClient.end();

    return NextResponse.json(
      { success: true, message: "Order placed successfully!" },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error in POST /api/transactions/order: ", error);
    return NextResponse.json(
      { success: false, message: "Internal server error!" },
      { status: 500 }
    );
  }
}
