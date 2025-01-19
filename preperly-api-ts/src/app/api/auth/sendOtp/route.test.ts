import { POST } from "../register/route";
import otpGenerator from "otp-generator";
import client from "@/lib/redisDb";

jest.mock("otp-generator");
jest.mock("@/lib/redisDb", () => ({
    setEx: jest.fn(),
}));
global.fetch = jest.fn();

describe("POST /api/auth/sendOtp", () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    it("should return 400 if mobileNumber is not provided", async () => {
        const request = new Request("http://localhost/api/auth/sendOtp", {
            method: "POST",
            body: JSON.stringify({}),
        });

        const response = await POST(request);
        const json = await response.json();

        expect(response.status).toBe(400);
        expect(json.message).toBe("Please fill in all the fields!");
    });

    it("should return 500 if API key or URL is not found", async () => {
        process.env.FAST2SMS_API_KEY = "";
        process.env.FAST2SMS_API_URL = "";

        const request = new Request("http://localhost/api/auth/sendOtp", {
            method: "POST",
            body: JSON.stringify({ mobileNumber: "1234567890" }),
        });

        const response = await POST(request);
        const json = await response.json();

        expect(response.status).toBe(500);
        expect(json.message).toBe("API key or URL not found");
    });

    it("should send OTP and return success response", async () => {
        process.env.FAST2SMS_API_KEY = "test_api_key";
        process.env.FAST2SMS_API_URL = "http://test.api.url";

        (otpGenerator.generate as jest.Mock).mockReturnValue("123456");
        (global.fetch as jest.Mock).mockResolvedValue({
            ok: true,
            json: jest.fn().mockResolvedValue({ message: "OTP sent" }),
        });

        const request = new Request("http://localhost/api/auth/sendOtp", {
            method: "POST",
            body: JSON.stringify({ mobileNumber: "1234567890" }),
        });

        const response = await POST(request);
        const json = await response.json();

        expect(response.status).toBe(200);
        expect(json.success).toBe(true);
        expect(json.message).toBe("OTP sent successfully");
        expect(client.setEx).toHaveBeenCalledWith("1234567890", 300, "123456");
    });

    it("should handle errors during OTP sending process", async () => {
        process.env.FAST2SMS_API_KEY = "test_api_key";
        process.env.FAST2SMS_API_URL = "http://test.api.url";

        (otpGenerator.generate as jest.Mock).mockReturnValue("123456");
        (global.fetch as jest.Mock).mockRejectedValue(new Error("Network error"));

        const request = new Request("http://localhost/api/auth/sendOtp", {
            method: "POST",
            body: JSON.stringify({ mobileNumber: "1234567890" }),
        });

        const response = await POST(request);
        const json = await response.json();

        expect(response.status).toBe(500);
        expect(json.success).toBe(false);
        expect(json.error).toBe("Error sending OTP");
    });
});