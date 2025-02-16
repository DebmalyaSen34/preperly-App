import { NextResponse } from "next/server";
import { Client } from "pg";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";

interface bodyData {
    phoneNumber: string;
    password: string;
}

export async function POST(request: Request): Promise<NextResponse> {
    try {
        const data: bodyData = await request.json();

        if (!data.phoneNumber || !data.password) {
            return NextResponse.json(
                {
                    success: false,
                    message: "Mobile number and password are required.",
                },
                {
                    status: 404,
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

        if (result.rows.length === 0) {
            return NextResponse.json(
                {
                    success: false,
                    message: "User not found.",
                },
                {
                    status: 404,
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
                    message: "Invalid password.",
                },
                {
                    status: 401,
                }
            );
        }

        // Check if JWT_SECRET is defined
        if (!process.env.JWT_SECRET) {
            return NextResponse.json(
                {
                    success: false,
                    message: "JWT_SECRET is not defined.",
                },
                {
                    status: 500,
                }
            );
        }

        // Generate JWT token
        const token = jwt.sign({ id: user.id }, process.env.JWT_SECRET, {
            expiresIn: "1m",
        });

        await cockroachDb.end();

        return NextResponse.json(
            {
                success: true,
                message: "User logged in successfully.",
                token: token,
            },
            {
                status: 200,
            }
        );
    } catch (err) {
        console.error("Error in POST /api/auth/user/login: ", err);
        return NextResponse.json(
            {
                success: false,
                message: "An error occurred.",
                error: (err as Error).message,
            },
            {
                status: 500,
            }
        );
    }
}
