import mongoose from "mongoose";
import { step1DataScheme, step2DataScheme, step1DataType, step2DataType } from "@/types/vendor";

interface VendorDocument extends mongoose.Document {
    step1Data: step1DataType,
    step2Data: step2DataType
}

const VendorSchema = new mongoose.Schema<VendorDocument>({
    step1Data: step1DataScheme,
    step2Data: step2DataScheme
});

const Vendor = mongoose.model<VendorDocument>("Vendors", VendorSchema);

export default Vendor;