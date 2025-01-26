import { step3 } from "@/app/api/auth/register/route";
import { uploadDocument } from "@/lib/uploadDocuments";
import client from "@/lib/redisDb";

jest.mock('@/lib/uploadDocuments');
jest.mock('@/lib/redisDb');

describe('step3 function', () => {
    const mockFormData = (fields: Record<string, string | File>) => {
        const formData = new FormData();
        for (const key in fields) {
            formData.append(key, fields[key]);
        }
        return formData;
    };

    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('should return 400 if any required field is missing', async () => {
        const formData = mockFormData({
            fssaiLicense: '123456',
            gstin: 'GSTIN123',
            panCard: 'PAN123',
            accountNumber: '1234567890',
            accountHolderName: 'John Doe'
            // Missing document files
        });

        const response = await step3(formData);
        expect(response.status).toBe(400);
        expect(await response.json()).toEqual({ success: false, message: "Please fill in all the fields!" });
    });

    it('should return 500 if document upload fails', async () => {
        (uploadDocument as jest.Mock).mockResolvedValueOnce({ success: false, error: 'Upload error' });

        const formData = mockFormData({
            fssaiLicense: '123456',
            gstin: 'GSTIN123',
            panCard: 'PAN123',
            accountNumber: '1234567890',
            accountHolderName: 'John Doe',
            fssaiDocument: new File([''], 'fssai.pdf'),
            gstinDocument: new File([''], 'gstin.pdf'),
            panCardDocument: new File([''], 'pan.pdf')
        });

        const response = await step3(formData);
        expect(response.status).toBe(500);
        expect(await response.json()).toEqual({
            success: false,
            message: "Error uploading documents",
            errors: {
                fssai: 'Upload error',
                gstin: undefined,
                panCard: undefined
            }
        });
    });

    it('should return 200 if all operations succeed', async () => {
        (uploadDocument as jest.Mock).mockResolvedValue({ success: true, fileId: 'fileId123' });
        (client.setEx as jest.Mock).mockResolvedValue(true);

        const formData = mockFormData({
            fssaiLicense: '123456',
            gstin: 'GSTIN123',
            panCard: 'PAN123',
            accountNumber: '1234567890',
            accountHolderName: 'John Doe',
            fssaiDocument: new File([''], 'fssai.pdf'),
            gstinDocument: new File([''], 'gstin.pdf'),
            panCardDocument: new File([''], 'pan.pdf')
        });

        const response = await step3(formData);
        expect(response.status).toBe(200);
        expect(await response.json()).toEqual({ success: true, message: "Documents uploaded successfully!" });
    });
});