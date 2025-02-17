import { NextResponse } from "next/server";
import { Client } from "pg";
import { corsHeaders, withCORS } from "@/utils/cors";

async function GET(request: Request) {
  try {
    const client = new Client(process.env.COCKROACH_DATABASE_URL);
    await client.connect();

    const userId = request.headers.get("userId");

    if (!userId) {
      return NextResponse.json(
        {
          success: false,
          message: "User id not provided",
        },
        {
          status: 400,
          headers: corsHeaders,
        }
      );
    }

    const result = await client.query("SELECT * FROM customers WHERE id = $1", [
      userId,
    ]);

    await client.end();

    if (result.rows.length === 0) {
      return NextResponse.json(
        {
          success: false,
          message: "User not found",
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
        message: "User info fetched successfully",
        data: result.rows[0],
      },
      {
        status: 200,
        headers: corsHeaders,
      }
    );
  } catch (error) {
    console.error("Error fetching user info:", error);
    return NextResponse.json(
      {
        success: false,
        message: "Internal server error",
      },
      {
        status: 500,
        headers: corsHeaders,
      }
    );
  }
}

export { GET };
export const OPTIONS = withCORS(GET);
