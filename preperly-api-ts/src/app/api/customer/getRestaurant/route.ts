import { NextResponse } from "next/server";
import { corsHeaders, withCORS } from "@/utils/cors";
import { supabase } from "@/lib/supbaseDb";

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

    const { data: restaurantData, error } = await supabase
      .from("vendor")
      .select("*")
      .eq("id", restaurantId);

    if (error) {
      console.error("Error fetching restaurant details", error);
      return NextResponse.json(
        { success: false, message: "Internal server error" },
        { status: 500, headers: corsHeaders }
      );
    }

    if (!restaurantData || restaurantData.length === 0) {
      return NextResponse.json(
        { success: false, message: "No restaurant found with this ID" },
        { status: 404, headers: corsHeaders }
      );
    }


    const { data: imagesData, error: imagesError } = await supabase
      .from("restaurantimages")
      .select("*")
      .eq("vendor_id", restaurantId);

    if (imagesError) {
      console.error("Error fetching restaurant images", imagesError);
      return NextResponse.json(
        { success: false, message: "Internal server error" },
        { status: 500, headers: corsHeaders }
      );
    }


    if (!imagesData || imagesData.length === 0) {
      return NextResponse.json(
        { success: false, message: "No restaurant found with this ID" },
        { status: 404, headers: corsHeaders }
      );
    }

    const { data: menuData, error: menuError } = await supabase
      .from("menuitems")
      .select("*")
      .eq("vendor_id", restaurantId);

    if (menuError) {
      console.error("Error fetching menu items", menuError);
      return NextResponse.json(
        { success: false, message: "No restaurant found with this ID" },
        { status: 404, headers: corsHeaders }
      );
    }

    if (!menuData || menuData.length === 0) {
      return NextResponse.json(
        { success: false, message: "No restaurant found with this ID" },
        { status: 404, headers: corsHeaders }
      );
    }

    return NextResponse.json(
      {
        success: true,
        data: {
          images: imagesData,
          menu: menuData,
          restaurant: restaurantData[0],
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
