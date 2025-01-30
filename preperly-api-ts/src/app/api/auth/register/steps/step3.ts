import { NextResponse } from "next/server";
import client from "@/lib/redisDb";
import { bucket } from "@/utils/gcsClient";

export default async function step3(formData: FormData): Promise<NextResponse> {
  try {
    const phoneNumber = formData.get("phoneNumber") as string;
    const fssaiLicense = formData.get("fssaiLicense") as string;
    const gstin = formData.get("gstin") as string;
    const panCard = formData.get("panCard") as string;
    const accountNumber = formData.get("accountNumber") as string;
    const accountHolderName = formData.get("accountHolderName") as string;
    const fssaiDocument = formData.get("fssaiDocument") as File;
    const gstinDocument = formData.get("gstinDocument") as File;
    const panCardDocument = formData.get("panCardDocument") as File;

    if (
      !fssaiLicense ||
      !gstin ||
      !panCard ||
      !accountNumber ||
      !accountHolderName ||
      !fssaiDocument ||
      !gstinDocument ||
      !panCardDocument
    ) {
      return NextResponse.json(
        { success: false, message: "Please fill in all the fields!" },
        { status: 400 }
      );
    }

    console.log("Received Documents: ", {
      fssaiDocument,
      gstinDocument,
      panCardDocument,
    });

    const uploadDocumentWithPhoneNumber = async (
      file: File,
      docType: string
    ) => {
      const extension = file.name.split(".").pop();
      const fileName = `${phoneNumber}/${docType}/document-${Date.now()}.${extension}`;

      const blob = bucket.file(fileName);
      const buffer = Buffer.from(await file.arrayBuffer());

      await blob.save(buffer, {
        metadata: {
          contentType: file.type,
        },
      });

      return `https://storage.googleapis.com/${bucket.name}/${fileName}`;
    };

    const user = await client.get(phoneNumber);

    if (!user) {
      return NextResponse.json(
        { success: false, message: "User not found!" },
        { status: 404 }
      );
    }

    const [fssaiDocumentUrl, gstinDocumentUrl, panCardDocumentUrl] =
      await Promise.all([
        uploadDocumentWithPhoneNumber(fssaiDocument, "fssai"),
        uploadDocumentWithPhoneNumber(gstinDocument, "gstin"),
        uploadDocumentWithPhoneNumber(panCardDocument, "panCard"),
      ]);

    const fssai_class = { license: fssaiLicense, url: fssaiDocumentUrl };
    const gstin_class = { number: gstin, url: gstinDocumentUrl };
    const pan_class = { number: panCard, url: panCardDocumentUrl };
    const bankAccount_class = {
      number: accountNumber,
      name: accountHolderName,
    };

    const userData = JSON.parse(user);

    userData.fssai = fssai_class;
    userData.gstin = gstin_class;
    userData.pan = pan_class;
    userData.bankAccount = bankAccount_class;

    await client.setEx(phoneNumber, 3600, JSON.stringify(userData));

    return NextResponse.json(
      {
        success: true,
        message: "Documents uploaded successfully!",
        documents: {
          fssaiUrl: fssaiDocumentUrl,
          gstinUrl: gstinDocumentUrl,
          panUrl: panCardDocumentUrl,
        },
      },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error in processing documents in step 3:", error);
    return NextResponse.json(
      { success: false, message: "Error uploading documents" },
      { status: 500 }
    );
  }
}
