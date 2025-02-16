import { NextResponse } from "next/server";
import { Client } from "pg";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import { corsHeaders, withCORS } from "@/utils/cors";

// Interface for request body
interface bodyData {
  phoneNumber: string;
  password: string;
}

async function POST(request: Request): Promise<NextResponse> {
  try {
    const data: bodyData = await request.json();

    // Check if phoneNumber and password are provided
    if (!data.phoneNumber || !data.password) {
      return NextResponse.json(
        {
          success: false,
          message: "Mobile number and password are required.",
        },
        {
          status: 400,
          headers: corsHeaders,
        }
      );
    }

    // Connect to CockroachDB
    const cockroachDb = new Client(process.env.COCKROACH_DATABASE_URL);
    await cockroachDb.connect();

    const query = `
            SELECT * FROM customers WHERE phonenumber = $1
        `;

    const values = [data.phoneNumber];

    const result = await cockroachDb.query(query, values);

    // Check if user exists
    if (result.rows.length === 0) {
      return NextResponse.json(
        {
          success: false,
          message: "Invalid credentials",
        },
        {
          status: 401,
          headers: corsHeaders,
        }
      );
    }

    const user = result.rows[0];

    // Check if password is correct
    const validPassword = await bcrypt.compare(data.password, user.password);

    if (!validPassword) {
      return NextResponse.json(
        {
          success: false,
          message: "Invalid credentials",
        },
        {
          status: 401,
          headers: corsHeaders,
        }
      );
    }

    // Check if JWT_SECRET is defined
    if (!process.env.JWT_SECRET) {
      console.error("JWT_SECRET is not defined.");
      return NextResponse.json(
        {
          success: false,
          message: "Internal server error.",
        },
        {
          status: 500,
          headers: corsHeaders,
        }
      );
    }

    // Generate JWT token
    const token = jwt.sign({ id: user.id }, process.env.JWT_SECRET, {
      expiresIn: "30m", // Change it to 30d after testing
    });

    await cockroachDb.end(); // Close connection

    return NextResponse.json(
      {
        success: true,
        message: "User logged in successfully.",
        token: token,
      },
      {
        status: 200,
        headers: corsHeaders,
      }
    );
  } catch (err) {
    console.error("Error in POST /api/auth/user/login: ", err);
    return NextResponse.json(
      {
        success: false,
        message: "Login failed. Please try again.",
      },
      {
        status: 500,
        headers: corsHeaders,
      }
    );
  }
}

export { POST };
export const OPTIONS = withCORS(POST);
