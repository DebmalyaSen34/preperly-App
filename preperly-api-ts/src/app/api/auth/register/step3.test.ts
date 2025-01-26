import { step3 } from "@/app/api/auth/register/route";
import { NextResponse } from "next/server";
import client from "@/lib/redisDb";

jest.mock("@/lib/redisDb");
jest.mock("@/utils/db");
jest.mock("@/models/vendor");
jest.mock("@/utils/gcsClient", () => ({
    bucket: jest.fn().mockReturnValue({
        file: jest.fn().mockReturnValue({
            save: jest.fn().mockResolvedValue(undefined),
        }),
    }),
}));

jest.mock("next/server", () => ({
    NextResponse: {
        json: jest.fn((data, init) => ({ data, ...init })),
    },
}));

describe("step3 function", () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    it("should return 400 if any required field is missing", async () => {
        const formData = new FormData();
        formData.append("phoneNumber", "1234567890");
        // Missing other fields

        const response = await step3(formData);
        expect(response).toEqual({ data: { success: false, message: "Please fill in all the fields!" }, status: 400 });
    });

    it("should return 404 if user is not found", async () => {
        const formData = new FormData();
        formData.append("phoneNumber", "1234567890");
        formData.append("fssaiLicense", "FSSAI123");
        formData.append("gstin", "GSTIN123");
        formData.append("panCard", "PAN123");
        formData.append("accountNumber", "123456789");
        formData.append("accountHolderName", "John Doe");
        formData.append("fssaiDocument", new File(["content"], "fssai.pdf", { type: "application/pdf" }));
        formData.append("gstinDocument", new File(["content"], "gstin.pdf", { type: "application/pdf" }));
        formData.append("panCardDocument", new File(["content"], "pan.pdf", { type: "application/pdf" }));

        (client.get as jest.Mock).mockResolvedValue(null);

        const response = await step3(formData);
        expect(response).toEqual({ data: { success: false, message: "User not found!" }, status: 404 });
    });

    it("should upload documents and update user data successfully", async () => {
        const formData = new FormData();
        formData.append("phoneNumber", "1234567890");
        formData.append("fssaiLicense", "FSSAI123");
        formData.append("gstin", "GSTIN123");
        formData.append("panCard", "PAN123");
        formData.append("accountNumber", "123456789");
        formData.append("accountHolderName", "John Doe");
        formData.append("fssaiDocument", new File(["content"], "fssai.png", { type: "image/png" }));
        formData.append("gstinDocument", new File(["content"], "gstin.png", { type: "image/png" }));
        formData.append("panCardDocument", new File(["content"], "pan.png", { type: "image/png" }));

        const mockUserData = JSON.stringify({ name: "Test User" });
        (client.get as jest.Mock).mockResolvedValue(mockUserData);
        (client.setEx as jest.Mock).mockResolvedValue(undefined);

        const response = await step3(formData);
        expect(response).toEqual({
            data: {
                success: true,
                message: "Documents uploaded successfully!",
                documents: {
                    fssaiUrl: expect.any(String),
                    gstinUrl: expect.any(String),
                    panUrl: expect.any(String),
                }
            },
            status: 200
        });
        expect(client.setEx).toHaveBeenCalled();
    });

    it("should handle errors gracefully", async () => {
        const formData = new FormData();
        formData.append("phoneNumber", "1234567890");
        formData.append("fssaiLicense", "FSSAI123");
        formData.append("gstin", "GSTIN123");
        formData.append("panCard", "PAN123");
        formData.append("accountNumber", "123456789");
        formData.append("accountHolderName", "John Doe");
        formData.append("fssaiDocument", new File(["content"], "fssai.pdf", { type: "application/pdf" }));
        formData.append("gstinDocument", new File(["content"], "gstin.pdf", { type: "application/pdf" }));
        formData.append("panCardDocument", new File(["content"], "pan.pdf", { type: "application/pdf" }));

        (client.get as jest.Mock).mockRejectedValue(new Error("Redis error"));

        const response = await step3(formData);
        expect(response).toEqual({ data: { success: false, message: "Error uploading documents" }, status: 500 });
    });
});