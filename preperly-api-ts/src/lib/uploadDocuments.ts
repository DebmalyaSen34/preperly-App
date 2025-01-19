import Documents from '@/models/documents';
import { Readable } from 'stream';
import { connectToDatabase, gridFsBucket } from '@/utils/db';

export async function uploadDocument(file: File) {
    try {
        await connectToDatabase();

        if (!file.name) {
            throw new Error("File name is required");
        }

        const buffer = await file.arrayBuffer();
        const stream = Readable.from(Buffer.from(buffer));

        const uploadStream = gridFsBucket.openUploadStream(file.name, {
            contentType: file.type,
        });

        const fileId = uploadStream.id;

        await new Promise((resolve, reject) => {
            stream.pipe(uploadStream)
                .on('error', reject)
                .on('finish', resolve);
        });

        const document = new Documents({
            fileName: file.name,
            contentType: file.type || 'application/octet-stream',
            length: file.size,
            uploadDate: new Date(),
            metadata: {
                originalName: file.name,
            },
        });

        await document.save();

        return { success: true, fileId: fileId.toString() };
    } catch (error) {
        console.error('Error uploading file:', error);
        return { success: false, error: error instanceof Error ? error.message : 'Internal server error' };
    }
}

