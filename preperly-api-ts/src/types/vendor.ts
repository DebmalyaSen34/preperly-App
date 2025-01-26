import { Schema } from "mongoose";

export interface step1DataType {
    restaurantName: string,
    restaurantAddress: string,
    phoneNumber: string,
    alternateNumber?: string,
    email: string,
    password: string,
    ownerName: string,
    ownerPhoneNumber: string,
    ownerEmail: string,
    recieveUpdatesOnWhatsApp?: boolean
}

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

export interface step2DataType {
    phoneNumber: string,
    timings: dateTimeDataType[]
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

const dateTimeDataSchema = new Schema<dateTimeDataType>({
    day: {
        type: String,
        enum: Object.values(dayofWeek),
        required: true
    },
    slots: [timeSlotSchema]
});

export const step1DataScheme = new Schema<step1DataType>({
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
        required: true,
        unique: true
    },
    alternateNumber: {
        type: String,
        required: false,
        unique: true
    },
    email: {
        type: String,
        required: true,
        unique: true
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
        required: true,
        unique: true
    },
    recieveUpdatesOnWhatsApp: {
        type: Boolean,
        required: false
    }
});

export const step2DataScheme = new Schema<step2DataType>({
    phoneNumber: {
        type: String,
        required: true,
        unique: true
    },
    timings: [dateTimeDataSchema]
});