import { NextResponse } from "next/server";
import client from "@/lib/redisDb";
import { bucket } from "@/utils/gcsClient";
import { menuItem } from "@/types/registration";

export default async function step5(formData: FormData): Promise<NextResponse> {
  try {
    const phoneNumber = formData.get("phoneNumber") as string;

    if (!phoneNumber) {
      return NextResponse.json(
        { success: false, message: "Phone number not provided!" },
        { status: 400 }
      );
    }

    console.log("====================================");
    console.log("phonenumber: ", phoneNumber);
    console.log("====================================");

    const user = (await client.get(phoneNumber)) as string;

    const userData = JSON.parse(user);

    // return error if no user found
    if (!user) {
      return NextResponse.json(
        { success: false, message: "User not found!" },
        { status: 404 }
      );
    }

    const menuItemsFile = formData.get("menuItems") as File;

    // return error if no menu items found
    if (!menuItemsFile) {
      return NextResponse.json(
        { success: false, message: "Please upload menu items!" },
        { status: 400 }
      );
    }

    const menuItemsString = await menuItemsFile.text();
    let menuItems: menuItem[];

    try {
      menuItems = JSON.parse(menuItemsString);

      // Check if menuItems is still a string
      if (typeof menuItems === "string") {
        menuItems = JSON.parse(menuItems);
      }
    } catch (error) {
      console.error("Failed to parse menu items:", error);
      return NextResponse.json(
        {
          success: false,
          message: "Failed to parse menu items!",
        },
        { status: 400 }
      );
    }

    if (!Array.isArray(menuItems)) {
      return NextResponse.json(
        {
          success: false,
          message: "Menu items should be an array!",
        },
        { status: 400 }
      );
    }

    console.log("Menu Items: ", menuItems);

    // Add unique id to each menu item
    const menuItemswithId = menuItems.map((item, index: number) => {
      return {
        ...item,
        id: `${phoneNumber}-${item.name.replace(" ", "_")}-${index}`,
      };
    });

    const menuImages = formData.getAll("menuImages") as Array<File>;

    // return error if no menu images found
    if (menuImages.length === 0) {
      return NextResponse.json(
        { success: false, message: "Please upload menu images!" },
        { status: 400 }
      );
    }

    // return error if number of menu images and menu items do not match
    if (menuImages.length !== menuItems.length) {
      return NextResponse.json(
        {
          success: false,
          message: "Number of menu images and menu items do not match!",
        },
        { status: 400 }
      );
    }

    // Upload the menu images to GCS
    const uploadMenuImagesWithPhoneNumber = async (
      file: File,
      itemId: string,
      itemName: string
    ) => {
      const extension = file.name.split(".").pop();
      const fileName = `${phoneNumber}/menuImages/${itemName}/image-${itemId}-${Date.now()}.${extension}`;

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
      menuImages.map((file, index) =>
        uploadMenuImagesWithPhoneNumber(
          file,
          menuItemswithId[index].id,
          menuItemswithId[index].name
        )
      )
    );

    menuItems.map((item, index) => {
      item.imageUrl = imageUrls[index];
    });

    // Push the menu items and menu images to the redis database
    await client.setEx(
      phoneNumber,
      3600,
      JSON.stringify({ userData, menuItems: menuItems })
    );

    console.log("====================================");
    console.log("Menu Items: ", menuItemswithId);
    console.log("====================================");
    console.log("Menu Images: ", menuImages);

    return NextResponse.json(
      {
        success: true,
        message: "Menu and menu images uploaded successfully!",
        menuItems,
        imageUrls,
      },
      {
        status: 200,
      }
    );
  } catch (error) {
    console.error("Error while uploading menu items", error);
    return NextResponse.json(
      { success: false, message: "Error uploading menu items" },
      { status: 500 }
    );
  }
}
