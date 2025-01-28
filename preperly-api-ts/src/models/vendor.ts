import mongoose, { Schema, Document } from "mongoose";

interface timeSlot {
    openTime: string,
    closeTime: string
}

enum dayofWeek {
    MONDAY = 'monday',
    TUESDAY = 'tuesday',
    WEDNESDAY = 'wednesday',
    THURSDAY = 'thursday',
    FRIDAY = 'friday',
    SATURDAY = 'saturday',
    SUNDAY = 'sunday'
}

interface dateTimeDataType {
    day: dayofWeek;
    slots: timeSlot[];
}

interface category {
    name: string,
    subCategories: string[]
}

interface menuItem {
    name: string,
    description: string,
    imageUrl: string,
    price: number,
    category: category,
    itemType: string,
    containsDairy: boolean,
}

interface VendorDocument extends Document {
    restaurantName: string,
    restaurantAddress: string,
    phoneNumber: string,
    alternateNumber?: string,
    email: string,
    password: string,
    ownerName: string,
    ownerPhoneNumber: string,
    ownerEmail: string,
    receiveUpdatesOnWhatsApp?: boolean,
    timings: dateTimeDataType[],
    fssai: {
        license: string,
        url: string
    },
    gstin: {
        number: string,
        url: string
    },
    pan: {
        number: string,
        url: string
    },
    bankAccount: {
        number: string,
        name: string
    },
    imageUrls: string[],
    logoUrl: string,
    approved: boolean,
    menu: menuItem[]
}

const timeSlotSchema = new Schema<timeSlot>({
    openTime: {
        type: String,
        required: true
    },
    closeTime: {
        type: String,
        required: true
    }
});

const dateTimeDataTypeSchema = new Schema<dateTimeDataType>({
    day: {
        type: String,
        enum: Object.values(dayofWeek),
        required: true
    },
    slots: {
        type: [timeSlotSchema],
        required: true
    }
});

interface category {
    name: string;
    subCategory: string[];
}

interface menuItem {
    name: string;
    price: number;
    description: string;
    imageUrl: string;
    category: category;
    itemType: string;
    containsDairy: boolean;
}

const categorySchema = new Schema<category>({
    name: {
        type: String,
        required: true
    },
    subCategories: {
        type: [String],
        required: true
    }
});

const menuItemSchema = new Schema<menuItem>({
    name: {
        type: String,
        required: true
    },
    description: {
        type: String,
        required: true
    },
    imageUrl: {
        type: String,
        required: true
    },
    price: {
        type: Number,
        required: true
    },
    category: {
        type: categorySchema,
        required: true
    },
    itemType: {
        type: String,
        required: true
    },
    containsDairy: {
        type: Boolean,
        required: true
    }
});

const VendorSchema = new Schema<VendorDocument>({
    restaurantName: {
        type: String,
        required: true
    },
    restaurantAddress: {
        type: String,
        required: true
    },
    phoneNumber: {
        type: String,
        required: true
    },
    alternateNumber: {
        type: String
    },

    email: {
        type: String,
        required: true
    },
    password: {
        type: String,
        required: true
    },
    ownerName: {
        type: String,
        required: true
    },
    ownerPhoneNumber: {
        type: String,
        required: true
    },
    ownerEmail: {
        type: String,
        required: true
    },
    receiveUpdatesOnWhatsApp: {
        type: Boolean
    },

    timings: {
        type: [dateTimeDataTypeSchema],
        required: true
    },
    fssai: {
        license: {
            type: String,
            required: true
        },
        url: {
            type: String,
            required: true
        }
    },
    gstin: {
        number: {
            type: String,
            required: true
        },
        url: {
            type: String,
            required: true
        }
    },
    pan: {
        number: {
            type: String,
            required: true
        },
        url: {
            type: String,
            required: true
        }
    },
    bankAccount: {
        number: {
            type: String,
            required: true
        },
        name: {
            type: String,
            required: true
        }
    },
    imageUrls: {
        type: [String],
        required: true
    },
    logoUrl: {
        type: String,
        required: true
    },
    approved: {
        type: Boolean,
        default: false
    },
    menu: {
        type: [menuItemSchema],
        required: true
    }
});

const Vendor = mongoose.models.Vendors || mongoose.model<VendorDocument>("Vendors", VendorSchema);

export default Vendor;