import { NextResponse } from "next/server";
import client from "@/lib/redisDb";
import * as dotenv from "dotenv";
import { menuItem } from "@/types/registration";
import { supabase } from "@/lib/supbaseDb";

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

    // await connectToDatabase();

    // // Saving data in mongoDB
    // const vendor = new Vendor({
    //   restaurantName: userData.restaurantName,
    //   restaurantAddress: userData.restaurantAddress,
    //   phoneNumber: userData.phoneNumber,
    //   alternateNumber: userData.alternateNumber,
    //   password: userData.password,
    //   email: userData.email,
    //   ownerName: userData.ownerName,
    //   ownerPhoneNumber: userData.ownerPhoneNumber,
    //   ownerEmail: userData.ownerEmail,
    //   receiveUpdatesOnWhatsApp: userData.receiveUpdatesOnWhatsApp,
    //   timings: userData.timings,
    //   fssai: userData.fssai,
    //   gstin: userData.gstin,
    //   pan: userData.pan,
    //   bankAccount: userData.bankAccount,
    //   imageUrls: userData.restaurantImagesUrl,
    //   logoUrl: userData.restaurantLogoUrl,
    //   menu: userData.menuItems,
    // });

    // await vendor.save();

    // Saving data in cockroachdb
    try {

      // insert basic restaurant info into vendor table

      const { data: vendorData, error: vendorError } = await supabase
        .from('vendor')
        .insert({
          restaurantname: userData.restaurantName,
          restaurantaddress: userData.restaurantAddress,
          password: userData.password,
          phonenumber: userData.phoneNumber,
          alternatenumber: userData.alternateNumber,
          email: userData.email,
          ownername: userData.ownerName,
          ownerphonenumber: userData.ownerPhoneNumber,
          owneremail: userData.ownerEmail,
          receiveupdatesonwhatsApp: userData.receiveUpdatesOnWhatsApp,
        })
        .select('id')
        .single();

      if (vendorError || !vendorData) {
        console.error("Error in inserting data into cockroachdb: ", vendorError);
        return NextResponse.json(
          {
            success: false,
            message: "Error in inserting data into cockroachdb",
          },
          { status: 500 }
        );
      }

      const vendorId = vendorData.id;

      console.log("====================================");
      console.log("vendorId: ", vendorId);
      console.log("====================================");

      // insert restaurant timings into timings table
      for (let i = 0; i < userData.timings.length; i++) {
        for (let j = 0; j < userData.timings[i].slots.length; j++) {
          const { error: timingError } = await supabase
            .from('timings')
            .insert({
              vendor_id: vendorId,
              day: userData.timings[i].day,
              opentime: userData.timings[i].slots[j].openTime,
              closetime: userData.timings[i].slots[j].closeTime,
            });

          if (timingError) {
            console.error("Error inserting timing data:", timingError);
            return NextResponse.json(
              {
                success: false,
                message: "Error inserting timing data into Supabase",
              },
              { status: 500 }
            );
          }
        }
      }

      // insert documents details into fssai table
      const { error: documentError } = await supabase
        .from('documents')
        .insert({
          vendor_id: vendorId,
          fssailicense: userData.fssai.license,
          fssaiurl: userData.fssai.url,
          gstinnumber: userData.gstin.number,
          gstinurl: userData.gstin.url,
          pannumber: userData.pan.number,
          panurl: userData.pan.url,
          bankaccountnumber: userData.bankAccount.number,
          bankaccountname: userData.bankAccount.name,
        });

      // check if data is inserted into cockroachdb
      if (documentError) {
        console.error("Error inserting document data:", documentError);
        return NextResponse.json(
          {
            success: false,
            message: "Error inserting document data into Supabase",
          },
          { status: 500 }
        );
      }

      // insert menuItems to the database
      for (let i = 0; i < userData.menuItems.length; i++) {
        const menuItem: menuItem = userData.menuItems[i];
        const { error: menuItemError } = await supabase
          .from('menuitems')
          .insert({
            vendor_id: vendorId,
            name: menuItem.name,
            description: menuItem.description,
            price: menuItem.price,
            category: menuItem.category,
            itemtype: menuItem.itemType,
            imageurl: menuItem.imageUrl,
            containsdairy: menuItem.containsDairy,
          });

        if (menuItemError) {
          console.error("Error inserting menu item:", menuItemError);
          return NextResponse.json(
            {
              success: false,
              message: "Error inserting menu item into Supabase",
            },
            { status: 500 }
          );
        }
      }

      // Insert restaurant images into the database
      const { error: imageError } = await supabase
        .from('restaurantimages')
        .insert({
          vendor_id: vendorId,
          imageurls: userData.restaurantImagesUrl,
          logourl: userData.restaurantLogoUrl,
        });

      if (imageError) {
        console.error("Error inserting restaurant images:", imageError);
        return NextResponse.json(
          {
            success: false,
            message: "Error inserting restaurant images into Supabase",
          },
          { status: 500 }
        );
      }
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
