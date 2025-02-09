import { NextResponse } from "next/server";
import client from "@/lib/redisDb";
import { bucket } from "@/utils/gcsClient";
import { v4 as uuidv4 } from "uuid";

export default async function step4(formData: FormData): Promise<NextResponse> {
  try {
    const phoneNumber = formData.get("phoneNumber") as string;
    const restaurantImages = formData.getAll("restaurantImages") as File[];
    const restuarantLogo = formData.get("restaurantLogo") as File;

    const user = await client.get(phoneNumber);

    if (!user) {
      return NextResponse.json(
        { success: false, message: "User not found!" },
        { status: 404 }
      );
    }

    const userData = JSON.parse(user);

    if (!restaurantImages || !restuarantLogo) {
      return NextResponse.json(
        { success: false, message: "Please upload images!" },
        { status: 400 }
      );
    }

    const uploadImagesWithPhoneNumber = async (
      file: File,
      imageType: string
    ) => {
      const extension = file.name.split(".").pop();
      const folder =
        imageType === "logo" ? "restaurantLogo" : "restaurantImages";
      const fileName = `${phoneNumber}/${folder}/image-${Date.now()}-${uuidv4()}.${extension}`;

      const blob = bucket.file(fileName);
      const buffer = Buffer.from(await file.arrayBuffer());

      await blob.save(buffer, {
        metadata: {
          contentType: file.type,
        },
      });

      return `https://storage.googleapis.com/${bucket.name}/${fileName}`;
    };

    const imageUrls = await Promise.all(
      restaurantImages.map((file) => uploadImagesWithPhoneNumber(file, "image"))
    );
    const logoUrl = await uploadImagesWithPhoneNumber(restuarantLogo, "logo");

    userData.restaurantImagesUrl = imageUrls;
    userData.restaurantLogoUrl = logoUrl;

    await client.setEx(phoneNumber, 3600, JSON.stringify(userData));

    return NextResponse.json(
      {
        success: true,
        message: "Images uploaded successfully!",
        imageUrls,
        logoUrl,
      },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error in processing images in step 4:", error);
    return NextResponse.json(
      { success: false, message: "Error uploading images" },
      { status: 500 }
    );
  }
}
