import { POST } from "@/app/api/auth/verifyOtp/route";
import client from "@/lib/redisDb";

jest.mock("@/lib/redisDb");

describe("POST /api/auth/verifyOtp", () => {
    let request: Request;

    beforeEach(() => {
        jest.clearAllMocks();
    });

    it("should return 400 if mobileNumber or otp is missing", async () => {
        request = new Request("http://localhost", {
            method: "POST",
            body: JSON.stringify({ mobileNumber: "" }),
        });

        const response = await POST(request);
        const json = await response.json();

        expect(response.status).toBe(400);
        expect(json.message).toBe("Please fill in all the fields!");
    });

    it("should return 400 if no OTP is found", async () => {
        (client.get as jest.Mock).mockResolvedValue(null);

        request = new Request("http://localhost", {
            method: "POST",
            body: JSON.stringify({ mobileNumber: "1234567890", otp: "123456" }),
        });

        const response = await POST(request);
        const json = await response.json();

        expect(response.status).toBe(400);
        expect(json.message).toBe("No OTP found!");
    });

    it("should return 400 if OTP is invalid", async () => {
        (client.get as jest.Mock).mockResolvedValue("654321");

        request = new Request("http://localhost", {
            method: "POST",
            body: JSON.stringify({ mobileNumber: "1234567890", otp: "123456" }),
        });

        const response = await POST(request);
        const json = await response.json();

        expect(response.status).toBe(400);
        expect(json.message).toBe("Invalid OTP!");
    });

    it("should return 200 if OTP is verified successfully", async () => {
        (client.get as jest.Mock).mockResolvedValue("123456");
        (client.del as jest.Mock).mockResolvedValue(true);

        request = new Request("http://localhost", {
            method: "POST",
            body: JSON.stringify({ mobileNumber: "1234567890", otp: "123456" }),
        });

        const response = await POST(request);
        const json = await response.json();

        expect(response.status).toBe(200);
        expect(json.message).toBe("OTP verified successfully!");
    });

    it("should return 500 if an error occurs", async () => {
        (client.get as jest.Mock).mockRejectedValue(new Error("Redis error"));

        request = new Request("http://localhost", {
            method: "POST",
            body: JSON.stringify({ mobileNumber: "1234567890", otp: "123456" }),
        });

        const response = await POST(request);
        const json = await response.json();

        expect(response.status).toBe(500);
        expect(json.message).toBe("An error occurred while verifying OTP!");
    });
});