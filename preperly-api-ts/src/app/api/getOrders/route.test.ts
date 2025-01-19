import { GET } from "@/app/api/getOrders/route";
import Order from "@/models/order";
import Vendor from "@/models/user";
import { connectToDatabase } from "@/utils/db";

jest.mock("@/models/order", () => ({
    __esModule: true,
    default: {
        find: jest.fn(),
    },
}));

jest.mock("@/models/user", () => ({
    __esModule: true,
    default: {
        findById: jest.fn(),
    },
}));

jest.mock("@/utils/db", () => ({
    connectToDatabase: jest.fn(),
}));

describe("GET /api/getOrders", () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    it("should return 404 if vendorId is missing", async () => {
        const request = new Request("http://localhost/api/getOrders");

        const response = await GET(request);
        const json = await response.json();

        expect(response.status).toBe(404);
        expect(json.message).toBe("VendorId is missing!");
    });

    it("should return 400 if vendorId is invalid", async () => {
        (Vendor.findById as jest.Mock).mockResolvedValue(null);

        const request = new Request("http://localhost/api/getOrders?vendorId=invalid");

        const response = await GET(request);
        const json = await response.json();

        expect(response.status).toBe(400);
        expect(json.message).toBe("Invalid vendorId!");
    });

    it("should return 404 if no orders are found", async () => {
        (Vendor.findById as jest.Mock).mockResolvedValue({ _id: "123" });
        (Order.find as jest.Mock).mockImplementation(() => ({
            sort: jest.fn(() => ({
                limit: jest.fn(() => []), // Simulate no orders found
            })),
        }));

        const request = new Request("http://localhost/api/getOrders?vendorId=123");

        const response = await GET(request);
        const json = await response.json();

        expect(response.status).toBe(404);
        expect(json.message).toBe("No orders found!");
    });

    it("should return 200 with orders if orders are found", async () => {
        (Vendor.findById as jest.Mock).mockResolvedValue({ _id: "123" });
        const mockOrders = [{ id: 1, name: "Order 1" }, { id: 2, name: "Order 2" }];
        (Order.find as jest.Mock).mockImplementation(() => ({
            sort: jest.fn(() => ({
                limit: jest.fn(() => mockOrders), // Simulate found orders
            })),
        }));

        const request = new Request("http://localhost/api/getOrders?vendorId=123");

        const response = await GET(request);
        const json = await response.json();

        expect(response.status).toBe(200);
        expect(json.message).toBe("Orders for your restaurant: ");
        expect(json.orders).toEqual(mockOrders);
    });

    it("should return 500 if an error occurs", async () => {
        (Vendor.findById as jest.Mock).mockResolvedValue({ _id: "123" });
        (Order.find as jest.Mock).mockImplementation(() => ({
            sort: jest.fn(() => ({
                limit: jest.fn(() => {
                    throw new Error("Database error");
                }),
            })),
        }));

        const request = new Request("http://localhost/api/getOrders?vendorId=123");

        const response = await GET(request);
        const json = await response.json();

        expect(response.status).toBe(500);
        expect(json.message).toBe("An error occurred while getting orders!");
    });

    it("should handle a large number of orders", async () => {
        (Vendor.findById as jest.Mock).mockResolvedValue({ _id: "123" });
        const mockOrders = Array.from({ length: 100 }, (_, i) => ({ id: i + 1, name: `Order ${i + 1}` }));
        (Order.find as jest.Mock).mockImplementation(() => ({
            sort: jest.fn(() => ({
                limit: jest.fn(() => mockOrders.slice(0, 10)), // Simulate pagination
            })),
        }));

        const request = new Request("http://localhost/api/getOrders?vendorId=123");

        const response = await GET(request);
        const json = await response.json();

        expect(response.status).toBe(200);
        expect(json.orders.length).toBe(10);
    });

    it("should return 500 if database connection fails", async () => {
        (connectToDatabase as jest.Mock).mockImplementation(() => {
            throw new Error("Database connection failed!");
        });

        const request = new Request("http://localhost/api/getOrders?vendorId=123");

        const response = await GET(request);
        const json = await response.json();

        expect(response.status).toBe(500);
        expect(json.message).toBe("Database connection failed!");
    });
});