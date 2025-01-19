import { NextRequest, NextResponse } from 'next/server';
import Documents from '@/models/documents';
import { Readable } from 'stream';
import { connectToDatabase, gridFsBucket } from '@/utils/db';

export async function POST(request: NextRequest) {
  try {
    await connectToDatabase();

    const formData = await request.formData();
    const file = formData.get('file') as File;

    if (!file) {
      return NextResponse.json({ error: 'No file uploaded' }, { status: 400 });
    }

    const buffer = await file.arrayBuffer();
    const stream = Readable.from(Buffer.from(buffer));

    const uploadStream = gridFsBucket.openUploadStream(file.name, {
      contentType: file.type,
    });

    await new Promise((resolve, reject) => {
      stream.pipe(uploadStream)
        .on('error', reject)
        .on('finish', resolve);
    });

    const document = new Documents({
      fileName: file.name,
      contentType: file.type,
      length: file.size,
      metadata: {
        originalName: file.name,
      },
    });

    await document.save();

    return NextResponse.json({ message: 'File uploaded successfully' }, { status: 200 });
  } catch (error) {
    console.error('Error uploading file:', error);
    return NextResponse.json({ error: 'Internal server error' }, { status: 500 });
  }
}