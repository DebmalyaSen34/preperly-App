import { NextResponse } from "next/server";
import Order from "@/models/order";
import Vendor from "@/models/vendor";
import { Client } from "pg";

async function checkVendorId(vendorId: string): Promise<boolean> {
    try {

        const vendor = await Vendor.findById(vendorId);

        return !!vendor;

    } catch (error) {
        console.error('Error checking vendorId:', error);
        return false;
    }
}


export async function GET(request: Request) {
    try {
        const url = new URL(request.url);
        const queryParameters = url.searchParams;

        const vendorId = queryParameters.get('vendorId');

        if (!vendorId) {
            return NextResponse.json({ message: "VendorId is missing!" }, { status: 404 });
        }

        console.log('vendorId:', vendorId);

        const cockroachClient = new Client(process.env.COCKROACH_DATABASE_URL);
        await cockroachClient.connect();

        console.log('Connected to CockroachDB');


        const orderQuery = `
            SELECT * FROM orders WHERE vendor_id = $1`

        const values = [vendorId];

        const result = await cockroachClient.query(orderQuery, values);

        console.log('result:', result.rows);

        await cockroachClient.end();

        // if (!await checkVendorId(vendorId)) {
        //     return NextResponse.json({ message: "Invalid vendorId!" }, { status: 400 });
        // }




        // const items_per_page = 10;

        // const orders = await Order.find({ restaurantId: vendorId }).sort({ createdAt: -1 }).limit(items_per_page);

        // if (orders.length === 0) {
        //     return NextResponse.json({ message: "No orders found!" }, { status: 404 });
        // }

        return NextResponse.json({ message: 'Orders for your restaurant: ', orders: result.rows }, { status: 200 });

    } catch (error) {
        console.error('Error getting orders:', error);
        if (error instanceof Error && error.message === 'Database connection failed!') {
            return NextResponse.json({ success: false, message: "Database connection failed!" }, { status: 500 });
        }
        return NextResponse.json({ success: false, message: "An error occurred while getting orders!" }, { status: 500 });
    }
}