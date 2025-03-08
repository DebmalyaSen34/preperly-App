import { NextResponse } from "next/server";
import { Client } from "pg";
import { corsHeaders, withCORS } from "@/utils/cors";

async function GET(request: Request): Promise<NextResponse> {
    try {
        const url = new URL(request.url);
        const queryParameters = url.searchParams;

        const customerId = queryParameters.get('customerId');

        if (!customerId) {
            return NextResponse.json(
                {
                    success: false,
                    message: "Customer ID is missing!"
                },
                { status: 404, headers: corsHeaders }
            )
        }

        console.log('customerId:', customerId);

        const cockraochClient = new Client(process.env.COCKROACH_DATABASE_URL);
        await cockraochClient.connect();

        console.log('Connected to CockroachDB');

        try {
            const result = await cockraochClient.query(
                'SELECT * FROM orders WHERE customer_id = $1',
                [customerId]
            );

            console.log('result:', result.rows);

            return NextResponse.json({
                success: true,
                data: result.rows
            }, { status: 200, headers: corsHeaders });
        } finally {
            await cockraochClient.end();
        }
    } catch (error) {
        console.error('Error fetching order history:', error);
        return NextResponse.json({
            success: false,
            message: 'Failed to fetch order history'
        }, { status: 500, headers: corsHeaders });
    }
}

export { GET };

export const OPTIONS = withCORS(GET);