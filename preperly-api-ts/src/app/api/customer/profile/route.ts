import { NextResponse } from "next/server";
import { supabase } from "@/lib/supbaseDb";
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

        // Get order history from supabase
        const { data: customerData, error } = await supabase
            .from('customers')
            .select('*')
            .eq('id', customerId);

        if (error) {
            console.error('Error fetching customer data:', error);
            return NextResponse.json(
                {
                    success: false,
                    message: 'Failed to fetch customer data'
                },
                { status: 500, headers: corsHeaders }
            )
        }

        return NextResponse.json({
            success: true,
            data: customerData
        }, { status: 200, headers: corsHeaders });

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