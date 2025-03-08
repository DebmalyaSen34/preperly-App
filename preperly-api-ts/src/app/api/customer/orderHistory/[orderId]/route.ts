import { NextResponse } from "next/server";
import { supabase } from "@/lib/supbaseDb";
import { corsHeaders, withCORS } from "@/utils/cors";

async function GET(request: Request, { params }: { params: { orderId: string } }): Promise<NextResponse> {
    try {

        if (!params.orderId) {
            return NextResponse.json(
                {
                    success: false,
                    message: "Order ID is missing!"
                },
                { status: 404, headers: corsHeaders }
            )
        }

        const orderId = params.orderId;

        console.log('customerId:', orderId);

        // Get order history from supabase
        const { data: order, error } = await supabase
            .from('orders')
            .select('*')
            .eq('id', orderId);

        if (error) {
            console.error('Error fetching order history:', error);
            return NextResponse.json(
                {
                    success: false,
                    message: 'Failed to fetch order with ID: ' + orderId
                },
                { status: 500, headers: corsHeaders }
            )
        }

        return NextResponse.json({
            success: true,
            data: order
        }, { status: 200, headers: corsHeaders });

    } catch (error) {
        console.error('Error fetching order history:', error);
        return NextResponse.json({
            success: false,
            message: 'Failed to fetch your order'
        }, { status: 500, headers: corsHeaders });
    }
}

export { GET };

export const OPTIONS = withCORS(GET);