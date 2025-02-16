import { NextResponse } from "next/server";
import { Client } from "pg";
import { corsHeaders, withCORS } from "@/utils/cors";

async function GET(request: Request): Promise<NextResponse> {
  try {
    const url = new URL(request.url);
    const queryParameters = url.searchParams;

    const { restaurantId } = Object.fromEntries(queryParameters.entries());

    if (!restaurantId) {
      return NextResponse.json(
        { success: false, message: "Restaurant ID not provided" },
        { status: 400, headers: corsHeaders }
      );
    }

    const client = new Client(process.env.COCKROACH_DATABASE_URL);
    await client.connect();

    const restaurantQuery = `
        select vendors.id, restaurantName from vendors where id = $1;
        `;
    const restaurantValues = [restaurantId];
    const restaurantResult = await client.query(
      restaurantQuery,
      restaurantValues
    );

    if (restaurantResult.rows.length === 0) {
      return NextResponse.json(
        { success: false, message: "No restaurant found with this ID" },
        { status: 404, headers: corsHeaders }
      );
    }

    const imagesQuery = `
        select * from restaurantImages where vendor_id = $1;
        `;
    const imagesValues = [restaurantId];
    const imagesResult = await client.query(imagesQuery, imagesValues);

    if (imagesResult.rows.length === 0) {
      return NextResponse.json(
        { success: false, message: "No images found for this restaurant" },
        { status: 404, headers: corsHeaders }
      );
    }

    const menuQuery = `
        select menuitems.id, vendor_id, name, description, imageurl, price from menuitems join vendors on menuitems.vendor_id = vendors.id where vendors.id = $1;
        `;
    const menuValues = [restaurantId];
    const menuResult = await client.query(menuQuery, menuValues);

    if (menuResult.rows.length === 0) {
      return NextResponse.json(
        { success: false, message: "No menu items found for this restaurant" },
        { status: 404, headers: corsHeaders }
      );
    }

    return NextResponse.json(
      {
        success: true,
        data: {
          images: imagesResult.rows,
          menu: menuResult.rows,
          restaurant: restaurantResult.rows[0],
        },
        message: "Restaurant details fetched successfully",
      },
      { status: 200, headers: corsHeaders }
    );
  } catch (error) {
    console.error("Error fetching restaurant details", error);
    return NextResponse.json(
      { success: false, message: "Internal server error" },
      { status: 500, headers: corsHeaders }
    );
  }
}

export { GET };

export const OPTIONS = withCORS(GET);
