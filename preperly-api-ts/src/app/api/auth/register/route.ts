import { NextResponse } from "next/server";
import bcrypt from "bcrypt";
import { connectToDatabase } from "@/utils/db";
import client from "@/lib/redisDb";
import Vendor from "@/models/vendor";
import { bucket } from "@/utils/gcsClient";
import { v4 as uuidv4 } from "uuid";

interface step1DataType {
  restaurantName: string;
  restaurantAddress: string;
  phoneNumber: string;
  alternateNumber?: string;
  email: string;
  password: string;
  ownerName: string;
  ownerPhoneNumber: string;
  ownerEmail: string;
  receiveUpdatesOnWhatsApp?: boolean;
}

interface timeSlot {
  openTime: string;
  closeTime: string;
}

interface dateTimeDataType {
  day:
    | "monday"
    | "tuesday"
    | "wednesday"
    | "thursday"
    | "friday"
    | "saturday"
    | "sunday";
  slots: timeSlot[];
}

interface step2DataType {
  phoneNumber: string;
  timings: dateTimeDataType[];
}

interface menuItem {
  name: string;
  description: string;
  price: string;
  // category: { name: string, subCategories: string[] },
  category: string;
  subCategory: string;
  itemType: string;
  imageUrl?: string;
  containsDairy: boolean;
}

//* Use redis to store the data and once all the data is stored, then save it to the database

// For each step different request body

//* Step 5: Save the menu data
//TODO: Save the menu data to the database

//* Step 6: Save the user data to the database
async function step6(phoneNumber: string): Promise<NextResponse> {
  try {
    if (!phoneNumber || phoneNumber === "") {
      return NextResponse.json(
        { success: false, message: "Phone number not provided!" },
        { status: 400 }
      );
    }

    const user = await client.get(phoneNumber);

    if (!user) {
      return NextResponse.json(
        { success: false, message: "User not found!" },
        { status: 404 }
      );
    }

    const userData = JSON.parse(user);

    await connectToDatabase();

    console.log("Step 3 data: ", userData.documentsUrl);

    const vendor = new Vendor({
      restaurantName: userData.restaurantName,
      restaurantAddress: userData.restaurantAddress,
      phoneNumber: userData.phoneNumber,
      alternateNumber: userData.alternateNumber,
      password: userData.password,
      email: userData.email,
      ownerName: userData.ownerName,
      ownerPhoneNumber: userData.ownerPhoneNumber,
      ownerEmail: userData.ownerEmail,
      receiveUpdatesOnWhatsApp: userData.receiveUpdatesOnWhatsApp,
      timings: userData.timings,
      fssai: userData.documentsUrl.fssai,
      gstin: userData.documentsUrl.gstin,
      pan: userData.documentsUrl.pan,
      bankAccount: userData.documentsUrl.bankAccount,
      imageUrls: userData.imageUrls,
      logoUrl: userData.logoUrl,
    });

    await vendor.save();

    await client.del(phoneNumber);

    return NextResponse.json(
      { success: true, message: "User registered successfully!" },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error in step 6:", error);
    return NextResponse.json(
      { success: false, message: "Internal server error in step 6" },
      { status: 500 }
    );
  }
}

//* Step 5: Push the menu and menu images

async function step5(formData: FormData): Promise<NextResponse> {
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

    const user = await client.get(phoneNumber) as string;

    const userData = JSON.parse(user);

    // return error if no user found
    if(!user){
        return NextResponse.json({ success: false, message: "User not found!" }, { status: 404 });
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
    let menuItems : menuItem[];

    try {
        menuItems = JSON.parse(menuItemsString);

        // Check if menuItems is still a string
      if (typeof menuItems === 'string') {
        menuItems = JSON.parse(menuItems);
      }
    } catch (error) {
        console.error("Failed to parse menu items:", error);
        return NextResponse.json({
            success: false,
            message: "Failed to parse menu items!",
        }, { status: 400});
    }

    if(!Array.isArray(menuItems)){
        return NextResponse.json({
            success: false,
            message: "Menu items should be an array!",
        }, { status: 400});
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
    if(menuImages.length === 0){
        return NextResponse.json({ success: false, message: "Please upload menu images!" }, { status: 400 });
    }

    // return error if number of menu images and menu items do not match
    if(menuImages.length !== menuItems.length){
        return NextResponse.json({ success: false, message: "Number of menu images and menu items do not match!" }, { status: 400 });
    }

    // Upload the menu images to GCS
    const uploadMenuImagesWithPhoneNumber = async (file : File, itemId: string, itemName: string) => {
        const extension = file.name.split('.').pop();
        const fileName = `${phoneNumber}/menuImages/${itemName}/image-${itemId}-${Date.now()}.${extension}`;

        const blob = bucket.file(fileName);
        const buffer = Buffer.from(await file.arrayBuffer());

        await blob.save(buffer, {
            metadata: {
                contentType: file.type
            }
        });

        return `https://storage.googleapis.com/${bucket.name}/${fileName}`;
    }

    const imageUrls = await Promise.all(menuImages.map((file, index) => uploadMenuImagesWithPhoneNumber(file, menuItemswithId[index].id, menuItemswithId[index].name)));

    menuItems.map((item, index) => {
        item.imageUrl = imageUrls[index];
    })

    // Push the menu items and menu images to the redis database
    await client.setEx(phoneNumber, 3600, JSON.stringify({ userData, menuItems: menuItems }));

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

//* ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- */

//* Step 4: Push restaurant images
async function step4(formData: FormData): Promise<NextResponse> {
  try {
    const phoneNumber = formData.get("phoneNumber") as string;
    const restaurantImages = formData.getAll("restaurantImages") as File[];
    const restuarantLogo = formData.get("restaurantLogo") as File;

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

    const user = await client.get(phoneNumber);

    if (!user) {
      return NextResponse.json(
        { success: false, message: "User not found!" },
        { status: 404 }
      );
    }

    const userData = JSON.parse(user);

    userData.imageUrls = imageUrls;
    userData.logoUrl = logoUrl;

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

//* ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- */

//* Step 3: Push restaurant documents
async function step3(formData: FormData): Promise<NextResponse> {
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

    const documentsUrl = {
      fssai: { license: fssaiLicense, url: fssaiDocumentUrl },
      gstin: { number: gstin, url: gstinDocumentUrl },
      pan: { number: panCard, url: panCardDocumentUrl },
      bankAccount: {
        number: accountNumber,
        name: accountHolderName,
      },
    };

    const userData = JSON.parse(user);

    await client.setEx(
      phoneNumber,
      3600,
      JSON.stringify({ ...userData, documentsUrl: documentsUrl })
    );

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

//* Step 2: Push restaurant Type and Timings
async function step2(step2Data: step2DataType): Promise<NextResponse> {
  try {
    if (!step2Data) {
      return NextResponse.json(
        { sucess: false, message: "Please fill in all the fields!" },
        { status: 400 }
      );
    }

    const { phoneNumber, timings } = step2Data;

    // Check if redis has this phoneNumber
    const user = await client.get(phoneNumber);

    if (!user) {
      return NextResponse.json(
        { success: false, message: "User not found!" },
        { status: 404 }
      );
    }

    const userData = JSON.parse(user);

    userData.timings = timings;

    await client.setEx(phoneNumber, 3600, JSON.stringify(userData));

    return NextResponse.json(
      { success: true, message: "Step 2 data saved successfully!" },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error in step 2:", error);
    return NextResponse.json(
      { success: false, message: "Internal server error" },
      { status: 500 }
    );
  }
}

//* Step 1: Push user information to redis
async function step1(step1Data: step1DataType): Promise<NextResponse> {
  try {
    const {
      restaurantName,
      restaurantAddress,
      phoneNumber,
      email,
      password,
      ownerName,
      ownerPhoneNumber,
      ownerEmail,
    }: step1DataType = step1Data;

    if (
      !restaurantName ||
      !restaurantAddress ||
      !phoneNumber ||
      !email ||
      !password ||
      !ownerName ||
      !ownerPhoneNumber ||
      !ownerEmail
    ) {
      return NextResponse.json(
        { message: "Please fill in all the fields!" },
        { status: 400 }
      );
    }

    const hashedPassword = bcrypt.hashSync(password, 10);

    if (await client.get(phoneNumber)) {
      return NextResponse.json(
        { message: "User already exists!" },
        { status: 400 }
      );
    }

    // Save the data to redis for now
    await client.setEx(
      phoneNumber,
      3600,
      JSON.stringify({ ...step1Data, password: hashedPassword })
    );

    return NextResponse.json(
      { success: true, message: "Step 1 data saved successfully!" },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error in step 1:", error);
    return NextResponse.json(
      { success: false, message: "Internal server error" },
      { status: 500 }
    );
  }
}

export async function POST(request: Request): Promise<NextResponse> {
  try {
    const url = new URL(request.url);
    const queryParameters = url.searchParams;

    const { step } = Object.fromEntries(queryParameters.entries());
    console.log(typeof step);

    switch (step) {
      case "1":
        const data: step1DataType = await request.json();
        return step1(data);
      case "2":
        const data2: step2DataType = await request.json();
        return step2(data2);
      case "3":
        const formData = await request.formData();
        return step3(formData);
      case "4":
        const formData4 = await request.formData();
        return step4(formData4);
      case "5":
        const formData5 = await request.formData();
        return step5(formData5);
      case "6":
        const { phoneNumber } = await request.json();
        return step6(phoneNumber);
    }

    return NextResponse.json(
      { success: true, message: "User registered successfully!" },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error registering user:", error);
    return NextResponse.json(
      { success: false, message: "Internal server error" },
      { status: 500 }
    );
  }
}
