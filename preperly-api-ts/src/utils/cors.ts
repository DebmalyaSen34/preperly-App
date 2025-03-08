import { NextResponse } from "next/server";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*", //! Replace with specific origins in production
  "Access-Control-Allow-Methods": "POST, OPTIONS, GET, PUT, DELETE",
  "Access-Control-Allow-Headers": "Content-Type, Authorization, userId",
};

export function withCORS(
  handler: (request: Request, context?: any) => Promise<NextResponse> | NextResponse
) {
  return async function OPTIONS(request: Request, context?: any): Promise<NextResponse> {
    if (request.method === "OPTIONS") {
      return new NextResponse(null, {
        status: 204,
        headers: corsHeaders,
      });
    }
    return handler(request, context);
  };
}

export { corsHeaders };
