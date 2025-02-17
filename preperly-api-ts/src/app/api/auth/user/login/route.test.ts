import { POST } from "@/app/api/auth/user/login/route";
import { Client } from "pg";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";

// Mock the necessary modules and functions
jest.mock("pg");
jest.mock("bcrypt");
jest.mock("jsonwebtoken");

describe("POST /api/auth/user/login", () => {
  let mockRequest: Request;
  let mockClient: jest.Mocked<Client>;

  beforeEach(() => {
    // Reset mocks before each test
    jest.clearAllMocks();

    // Mock the request object
    mockRequest = {
      json: jest.fn(),
    } as unknown as Request;

    // Mock the database client
    mockClient = {
      connect: jest.fn(),
      query: jest.fn(),
      end: jest.fn(),
    } as unknown as jest.Mocked<Client>;

    (Client as unknown as jest.Mock).mockImplementation(() => mockClient);
  });

  it("should return 400 if phoneNumber or password is not provided", async () => {
    (mockRequest.json as jest.Mock).mockResolvedValue({});

    const response = await POST(mockRequest);

    expect(response.status).toBe(400);
    expect(await response.json()).toEqual({
      success: false,
      message: "Mobile number and password are required.",
    });
  });

  it("should return 401 if user is not found", async () => {
    (mockRequest.json as jest.Mock).mockResolvedValue({
      phoneNumber: "1234567890",
      password: "password",
    });
    (mockClient.query as jest.Mock).mockResolvedValue({ rows: [] });

    const response = await POST(mockRequest);

    expect(response.status).toBe(401);
    expect(await response.json()).toEqual({
      success: false,
      message: "Invalid credentials",
    });
  });

  it("should return 401 if password is invalid", async () => {
    (mockRequest.json as jest.Mock).mockResolvedValue({
      phoneNumber: "1234567890",
      password: "password",
    });
    (mockClient.query as jest.Mock).mockResolvedValue({
      rows: [
        {
          id: "user-id",
          phoneNumber: "1234567890",
          password: "hashedPassword",
        },
      ],
    });
    (bcrypt.compare as jest.Mock).mockResolvedValue(false);

    const response = await POST(mockRequest);

    expect(response.status).toBe(401);
    expect(await response.json()).toEqual({
      success: false,
      message: "Invalid credentials",
    });
  });

  it("should return 500 if JWT_SECRET is not defined", async () => {
    (mockRequest.json as jest.Mock).mockResolvedValue({
      phoneNumber: "1234567890",
      password: "password",
    });
    (mockClient.query as jest.Mock).mockResolvedValue({
      rows: [
        {
          id: "user-id",
          phoneNumber: "1234567890",
          password: "hashedPassword",
        },
      ],
    });
    (bcrypt.compare as jest.Mock).mockResolvedValue(true);
    delete process.env.JWT_SECRET; // Unset JWT_SECRET

    const response = await POST(mockRequest);

    expect(response.status).toBe(500);
    expect(await response.json()).toEqual({
      success: false,
      message: "Internal server error.",
    });

    // Restore JWT_SECRET
    process.env.JWT_SECRET = "test-secret";
  });

  it("should return 200 with a token if login is successful", async () => {
    (mockRequest.json as jest.Mock).mockResolvedValue({
      phoneNumber: "1234567890",
      password: "password",
    });
    (mockClient.query as jest.Mock).mockResolvedValue({
      rows: [
        {
          id: "user-id",
          phoneNumber: "1234567890",
          password: "hashedPassword",
        },
      ],
    });
    (bcrypt.compare as jest.Mock).mockResolvedValue(true);
    (jwt.sign as jest.Mock).mockReturnValue("mocked-token");
    process.env.JWT_SECRET = "test-secret"; // Set JWT_SECRET

    const response = await POST(mockRequest);

    expect(response.status).toBe(200);
    expect(await response.json()).toEqual({
      success: true,
      message: "User logged in successfully.",
      token: "mocked-token",
    });
    expect(jwt.sign).toHaveBeenCalledWith({ id: "user-id" }, "test-secret", {
      expiresIn: "15d",
    });
  });

  it("should handle errors and return 500 status", async () => {
    (mockRequest.json as jest.Mock).mockRejectedValue(new Error("Test error"));

    const response = await POST(mockRequest);

    expect(response.status).toBe(500);
    expect(await response.json()).toEqual({
      success: false,
      message: "Login failed. Please try again.",
    });
  });
});
