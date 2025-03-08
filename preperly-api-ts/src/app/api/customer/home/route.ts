import { NextResponse } from "next/server";
import { supabase } from "@/lib/supbaseDb";
import { corsHeaders, withCORS } from "@/utils/cors";

async function GET(): Promise<NextResponse> {
  try {

    const { data: result, error } = await supabase
      .from("vendor")
      .select("*");


    if (error) {
      console.error("Error fetching vendors: ", error);
      return NextResponse.json(
        {
          success: false,
          data: [],
          message: "No vendors available",
        },
        {
          status: 404,
          headers: corsHeaders,
        }
      );
    }

    console.log("====================================");
    console.log("result: ", result);
    console.log("====================================");

    if (result.length === 0) {
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
        data: result,
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
