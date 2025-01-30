import { NextResponse } from "next/server";
import step1 from "./steps/step1";
import step2 from "./steps/step2";
import step3 from "./steps/step3";
import step4 from "./steps/step4";
import step5 from "./steps/step5";
import step6 from "./steps/step6";
import { step1DataType } from "@/types/registration";
import { step2DataType } from "@/types/registration";

export async function POST(request: Request): Promise<NextResponse> {
  try {
    const url = new URL(request.url);
    const queryParameters = url.searchParams;

    const { step } = Object.fromEntries(queryParameters.entries());
    console.log(typeof step);

    switch (step) {
      case "1":
        const data: step1DataType = await request.json();
        return step1(data);
      case "2":
        const data2: step2DataType = await request.json();
        return step2(data2);
      case "3":
        const formData = await request.formData();
        return step3(formData);
      case "4":
        const formData4 = await request.formData();
        return step4(formData4);
      case "5":
        const formData5 = await request.formData();
        return step5(formData5);
      case "6":
        const { phoneNumber } = await request.json();
        return step6(phoneNumber);
    }

    return NextResponse.json(
      { success: true, message: "User registered successfully!" },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error registering user:", error);
    return NextResponse.json(
      { success: false, message: "Internal server error" },
      { status: 500 }
    );
  }
}
