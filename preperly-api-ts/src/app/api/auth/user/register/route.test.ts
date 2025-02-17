import { POST } from "@/app/api/auth/user/register/route";
import { Client } from "pg";
import bcrypt from "bcrypt";
import redisClient from "@/lib/redisDb";
import otpGenerator from "otp-generator";

jest.mock("pg");
jest.mock("bcrypt");
jest.mock("@/lib/redisDb", () => ({
  setEx: jest.fn(),
}));
jest.mock("otp-generator");

describe("POST /api/auth/user/register", () => {
  let mockRequest: Request;
  let mockClient: jest.Mocked<Client>;

  beforeEach(() => {
    jest.clearAllMocks();

    mockRequest = {
      json: jest.fn(),
    } as unknown as Request;

    mockClient = {
      connect: jest.fn(),
      query: jest.fn(),
      end: jest.fn(),
    } as unknown as jest.Mocked<Client>;

    (Client as unknown as jest.Mock).mockImplementation(() => mockClient);
    process.env.FAST2SMS_API_KEY = "test_api_key";
    process.env.FAST2SMS_API_URL = "https://test.fast2sms.com";
  });

  it("should return 400 if data is not provided", async () => {
    (mockRequest.json as jest.Mock).mockResolvedValue(null);

    const response = await POST(mockRequest);

    expect(response.status).toBe(400);
    expect(await response.json()).toEqual({
      success: false,
      message: "Data not provided",
    });
  });

  it("should return 400 if user already exists", async () => {
    (mockRequest.json as jest.Mock).mockResolvedValue({
      phoneNumber: "1234567890",
      password: "password",
    });
    (mockClient.query as jest.Mock).mockResolvedValue({ rows: [{}] });

    const response = await POST(mockRequest);

    expect(response.status).toBe(400);
    expect(await response.json()).toEqual({
      success: false,
      message: "User already exists",
    });
  });

  it("should return 500 if Fast2SMS API key or URL is not provided", async () => {
    (mockRequest.json as jest.Mock).mockResolvedValue({
      phoneNumber: "1234567890",
      password: "password",
    });
    (mockClient.query as jest.Mock).mockResolvedValue({ rows: [] });
    process.env.FAST2SMS_API_KEY = "";
    process.env.FAST2SMS_API_URL = "";

    const response = await POST(mockRequest);

    expect(response.status).toBe(500);
    expect(await response.json()).toEqual({
      success: false,
      error: "Fast2SMS API key or URL not provided",
    });
  });

  it("should successfully register user and send OTP", async () => {
    (mockRequest.json as jest.Mock).mockResolvedValue({
      phoneNumber: "1234567890",
      password: "password",
    });
    (mockClient.query as jest.Mock).mockResolvedValue({ rows: [] });
    (bcrypt.genSalt as jest.Mock).mockResolvedValue("salt");
    (bcrypt.hash as jest.Mock).mockResolvedValue("hashedPassword");
    (otpGenerator.generate as jest.Mock).mockReturnValue("123456");
    (redisClient.setEx as jest.Mock).mockResolvedValue(undefined);

    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve({ success: true }),
      })
    ) as jest.Mock;

    const response = await POST(mockRequest);

    expect(response.status).toBe(200);
    expect(await response.json()).toEqual({
      success: true,
      message: "OTP successfully sent to your mobile number!",
    });
  });

  it("should handle errors when sending OTP fails", async () => {
    (mockRequest.json as jest.Mock).mockResolvedValue({
      phoneNumber: "1234567890",
      password: "password",
    });
    (mockClient.query as jest.Mock).mockResolvedValue({ rows: [] });
    (bcrypt.genSalt as jest.Mock).mockResolvedValue("salt");
    (bcrypt.hash as jest.Mock).mockResolvedValue("hashedPassword");
    (otpGenerator.generate as jest.Mock).mockReturnValue("123456");
    (redisClient.setEx as jest.Mock).mockResolvedValue(undefined);

    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: false,
        json: () => Promise.resolve({ success: false, error: "Failed" }),
      })
    ) as jest.Mock;

    const response = await POST(mockRequest);

    expect(response.status).toBe(500);
    expect(await response.json()).toEqual({
      error: "Failed to send OTP",
      smsData: { success: false, error: "Failed" },
    });
  });

  it("should handle errors during registration process", async () => {
    (mockRequest.json as jest.Mock).mockRejectedValue(new Error("Test error"));

    const response = await POST(mockRequest);

    expect(response.status).toBe(500);
    expect(await response.json()).toEqual({
      success: false,
      message: "Internal server error",
    });
  });
});
