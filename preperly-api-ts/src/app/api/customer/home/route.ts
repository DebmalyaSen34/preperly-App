import { NextResponse } from "next/server";
import { Client } from "pg";
import { corsHeaders, withCORS } from "@/utils/cors";

async function GET(): Promise<NextResponse> {
  try {
    const client = new Client(process.env.COCKROACH_DATABASE_URL);
    await client.connect();

    const query = `
        select vendors.id, restaurantname, restaurantaddress, logourl from vendors join restaurantimages on vendors.id = restaurantimages.vendor_id;
        `;

    const result = await client.query(query);

    await client.end();

    if (result.rows.length === 0) {
      return NextResponse.json(
        {
          success: false,
          data: [],
          message: "No restaurants to show",
        },
        {
          status: 404,
          headers: corsHeaders,
        }
      );
    }

    return NextResponse.json(
      {
        success: true,
        data: result.rows,
        message: "Vendors fetched successfully",
      },
      {
        status: 200,
        headers: corsHeaders,
      }
    );
  } catch (error) {
    console.error("There was an error while fetching restaurants!", error);
    return NextResponse.json(
      { success: false, message: "Failed to fetch vendors" },
      { status: 500, headers: corsHeaders }
    );
  }
}

export { GET };
export const OPTIONS = withCORS(GET);
