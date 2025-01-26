import mongoose from "mongoose";
import { Schema, Document } from "mongoose";

interface IDocument extends Document {
    fileName: string;
    contentType: string;
    length: number;
    uploadDate: Date;
    metadata: any;
}

const DocumentSchema = new Schema({
    fileName: { type: String, required: true },
    contentType: { type: String, required: true },
    length: { type: Number, required: true },
    uploadDate: { type: Date, default: Date.now },
    metadata: { type: Schema.Types.Mixed }
});

const Documents = mongoose.model<IDocument>('Document', DocumentSchema);
export default Documents;