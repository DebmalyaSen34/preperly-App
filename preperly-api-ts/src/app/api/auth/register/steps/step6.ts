import { NextResponse } from "next/server";
import client from "@/lib/redisDb";
import { connectToDatabase } from "@/utils/db";
import Vendor from "@/models/vendor";
import { Client } from "pg";
import * as dotenv from "dotenv";
import { menuItem } from "@/types/registration";

dotenv.config({ path: ".env.local" });

export default async function step6(
  phoneNumber: string
): Promise<NextResponse> {
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

    console.log("====================================");
    console.log("userData: ", userData);
    console.log("====================================");

    await connectToDatabase();

    // Saving data in mongoDB
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
      fssai: userData.fssai,
      gstin: userData.gstin,
      pan: userData.pan,
      bankAccount: userData.bankAccount,
      imageUrls: userData.restaurantImagesUrl,
      logoUrl: userData.restaurantLogoUrl,
      menu: userData.menuItems,
    });

    await vendor.save();

    // Saving data in cockroachdb
    try {
      const cockraochClient = new Client(process.env.COCKROACH_DATABASE_URL);
      await cockraochClient.connect();

      // insert basic restaurant info into vendor table
      const insertQuery = `
      INSERT INTO vendors (
      restaurantName,
      restaurantAddress,
      phoneNumber,
      alternateNumber,
      password,
      email,
      ownerName,
      ownerPhoneNumber,
      ownerEmail,
      receiveUpdatesOnWhatsApp
      )
      VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
      `;

      const values = [
        userData.restaurantName,
        userData.restaurantAddress,
        userData.phoneNumber,
        userData.alternateNumber,
        userData.password,
        userData.email,
        userData.ownerName,
        userData.ownerPhoneNumber,
        userData.ownerEmail,
        userData.receiveUpdatesOnWhatsApp,
      ];

      const result = await cockraochClient.query(insertQuery, values);

      console.log("====================================");
      console.log("result: ", result);
      console.log("====================================");

      // check if data is inserted into cockroachdb
      if (result.rowCount === 0) {
        return NextResponse.json(
          {
            success: false,
            message: "Error in inserting data into cockroachdb",
          },
          { status: 500 }
        );
      }

      // get vendorId from vendor table
      const getVendorIdQuery = `
      SELECT id FROM vendors WHERE phoneNumber = $1
      `;

      const vendorId = await cockraochClient.query(getVendorIdQuery, [
        userData.phoneNumber,
      ]);

      console.log("====================================");
      console.log("vendorId: ", vendorId);
      console.log("====================================");

      // insert restaurant timings into timings table
      const insertTimingsQuery = `
      INSERT INTO timings (
      vendor_id,
      day,
      openTime,
      closeTime
      )
      VALUES ($1, $2, $3, $4)`;

      for (let i = 0; i < userData.timings.length; i++) {
        for (let j = 0; j < userData.timings[i].slots.length; j++) {
          const values = [
            vendorId.rows[0].id,
            userData.timings[i].day,
            userData.timings[i].slots[j].openTime,
            userData.timings[i].slots[j].closeTime,
          ];

          const result = await cockraochClient.query(
            insertTimingsQuery,
            values
          );

          // check if data is inserted into cockroachdb

          if (result.rowCount === 0) {
            return NextResponse.json(
              {
                success: false,
                message: "Error in inserting data into cockroachdb",
              },
              { status: 500 }
            );
          }
        }
      }

      // insert documents details into fssai table
      const insertDocumentsQuery = `
        INSERT INTO documents(
          vendor_id,
          fssailicense,
          fssaiurl,
          gstinnumber,
          gstinurl,
          pannumber,
          panurl,
          bankaccountnumber,
          bankaccountname
        )
          VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
      `;

      const documentValues = [
        vendorId.rows[0].id,
        userData.fssai.license,
        userData.fssai.url,
        userData.gstin.number,
        userData.gstin.url,
        userData.pan.number,
        userData.pan.url,
        userData.bankAccount.number,
        userData.bankAccount.name,
      ];

      const documentResult = await cockraochClient.query(
        insertDocumentsQuery,
        documentValues
      );

      // check if data is inserted into cockroachdb
      if (documentResult.rowCount === 0) {
        return NextResponse.json(
          {
            success: false,
            message: "Error in inserting data into cockroachdb",
          },
          { status: 500 }
        );
      }

      // insert menuItems to the database
      const insertMenuItemsQuery = `
      INSERT INTO menuitems (
        vendor_id,
        name,
        description,
        price,
        category,
        itemtype,
        imageUrl,
        containsdairy
      )
      VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
      `;

      for (let i = 0; i < userData.menuItems.length; i++) {
        const menuItem: menuItem = userData.menuItems[i];
        const values = [
          vendorId.rows[0].id,
          menuItem.name,
          menuItem.description,
          menuItem.price,
          menuItem.category,
          menuItem.itemType,
          menuItem.imageUrl,
          menuItem.containsDairy,
        ];

        const result = await cockraochClient.query(
          insertMenuItemsQuery,
          values
        );

        // check if data is inserted into cockroachdb
        if (result.rowCount === 0) {
          return NextResponse.json(
            {
              success: false,
              message: "Error in inserting data into cockroachdb",
            },
            { status: 500 }
          );
        }
      }

      // Insert restaurant images into the database
      const insertImagesQuery = `
      INSERT INTO restaurantimages (
        vendor_id,
        imageurls,
        logourl
      )
      VALUES ($1, $2, $3)
      `;

      const imagesValues = [
        vendorId.rows[0].id,
        userData.restaurantImagesUrl,
        userData.restaurantLogoUrl,
      ];

      const imagesResult = await cockraochClient.query(
        insertImagesQuery,
        imagesValues
      );

      // check if data is inserted into cockroachdb
      if (imagesResult.rowCount === 0) {
        return NextResponse.json(
          {
            success: false,
            message: "Error in inserting data into cockroachdb",
          },
          { status: 500 }
        );
      }

      await cockraochClient.end();
    } catch (error) {
      console.error("Error in inserting data into cockroachdb: ", error);
      return NextResponse.json(
        {
          success: false,
          message: "Internal server error in inserting data into cockroachdb",
        },
        { status: 500 }
      );
    }

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
