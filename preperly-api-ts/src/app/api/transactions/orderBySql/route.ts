import { NextResponse } from "next/server";
import { Client } from "pg";
import * as dotenv from "dotenv";
import QRCode from "qrcode";
import { v4 as uuidv4 } from "uuid";
dotenv.config({ path: ".env.local" });
import { corsHeaders, withCORS } from "@/utils/cors";

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

async function POST(request: Request): Promise<NextResponse> {
  try {
    const data: RequestData = await request.json();

    if (!data) {
      return NextResponse.json(
        { success: false, message: "Data is missing!" },
        { status: 404, headers: corsHeaders }
      );
    }

    console.log("====================================");
    console.log("data: ", data);
    console.log("====================================");

    // Connect to CockroachDB
    const cockraochClient = new Client(process.env.COCKROACH_DATABASE_URL);
    await cockraochClient.connect();

    const orderId = uuidv4();
    const qrCodeData = await QRCode.toDataURL(orderId);

    // Insert order data into CockroachDB
    const orderQuery = `
      INSERT INTO orders (id, vendor_id, customer_id, arrivaltime, ordertype, items, orderstatus, totalamount, totalquantity, qrcode)
      VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
    `;

    const values = [
      orderId,
      data.vendorId,
      data.customerId,
      data.arrivalTime,
      data.orderType,
      data.items,
      data.orderStatus,
      data.totalAmount,
      data.totalQuantity,
      qrCodeData,
    ];

    const result = await cockraochClient.query(orderQuery, values);

    // Check if data is inserted into CockroachDB
    if (result.rowCount === 0) {
      return NextResponse.json(
        { success: false, message: "Error in inserting data into CockroachDB" },
        { status: 500, headers: corsHeaders }
      );
    }

    await cockraochClient.end();

    return NextResponse.json(
      {
        success: true,
        message: "Order placed successfully!",
        qr: qrCodeData,
        orderId: orderId,
      },
      { status: 200, headers: corsHeaders }
    );
  } catch (error) {
    console.error("Error in POST /api/transactions/order: ", error);
    return NextResponse.json(
      {
        success: false,
        message: "Internal server error!",
      },
      { status: 500, headers: corsHeaders }
    );
  }
}

export { POST };

export const OPTIONS = withCORS(POST);
