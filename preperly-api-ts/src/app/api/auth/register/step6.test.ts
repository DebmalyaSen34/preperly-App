import { step6 } from "@/app/api/auth/register/route";
import client from "@/lib/redisDb";
import Vendor from "@/models/vendor";

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

describe("step6 function", () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    it("should return 400 if phone number is not provided", async () => {
        const response = await step6("");
        expect(response).toEqual({ data: { success: false, message: "Phone number not provided!" }, status: 400 });
    });

    it("should return 404 if user is not found", async () => {
        (client.get as jest.Mock).mockResolvedValue(null);
        const response = await step6("1234567890");
        expect(response).toEqual({ data: { success: false, message: "User not found!" }, status: 404 });
    });

    it("should register user successfully", async () => {
        const mockUserData = JSON.stringify({
            restaurantName: "Test Restaurant",
            restaurantAddress: "123 Test St",
            phoneNumber: "1234567890",
            alternateNumber: "0987654321",
            password: "hashedPassword",
            email: "test@example.com",
            ownerName: "Test Owner",
            ownerPhoneNumber: "0987654321",
            ownerEmail: "owner@example.com",
            receiveUpdatesOnWhatsApp: true,
            timings: "9 AM - 9 PM",
            documentsUrl: {
                fssai: "fssaiUrl",
                gstin: "gstinUrl",
                pan: "panUrl",
                bankAccount: "bankAccountUrl"
            },
            imageUrls: ["imageUrl1", "imageUrl2"],
            logoUrl: "logoUrl"
        });

        (client.get as jest.Mock).mockResolvedValue(mockUserData);
        (Vendor.prototype.save as jest.Mock).mockResolvedValue(undefined);
        (client.del as jest.Mock).mockResolvedValue(undefined);

        const response = await step6("1234567890");
        expect(response).toEqual({ data: { success: true, message: "User registered successfully!" }, status: 200 });
        expect(Vendor.prototype.save).toHaveBeenCalled();
        expect(client.del).toHaveBeenCalledWith("1234567890");
    });

    it("should handle errors gracefully", async () => {
        (client.get as jest.Mock).mockRejectedValue(new Error("Redis error"));
        const response = await step6("1234567890");
        expect(response).toEqual({ data: { success: false, message: "Internal server error in step 6" }, status: 500 });
    });
});