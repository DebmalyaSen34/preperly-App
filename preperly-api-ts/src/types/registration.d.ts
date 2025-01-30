export interface step1DataType {
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

export interface step2DataType {
  phoneNumber: string;
  timings: dateTimeDataType[];
}

export interface menuItem {
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
