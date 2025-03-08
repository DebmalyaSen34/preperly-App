import { NextResponse } from "next/server";
import bcrypt from "bcrypt";
import { supabase } from "@/lib/supbaseDb";
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

    // Connect to supabase
    const { data: result, error } = await supabase
      .from("customers")
      .select("*")
      .eq("phonenumber", data.phoneNumber);

    if (error) {
      console.error("Error fetching user:", error);
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

    const user = result && result.length > 0 ? result[0] : null;

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
      expiresIn: "15d", //! Change it to 30d after testing
    });

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
