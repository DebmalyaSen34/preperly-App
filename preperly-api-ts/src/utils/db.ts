import mongoose from "mongoose";
import { GridFSBucket } from "mongodb";

const URI: string | undefined = process.env.MONGODB_URI;

if (!URI) {
    throw new Error(
        "Please define the MONGODB_URI variable inside .env.local"
    );
}

interface MongooseCache {
    conn: mongoose.Connection | null,
    promise: Promise<mongoose.Connection> | null;
}

const globalWithMongoose = global as typeof global & { mongoose: MongooseCache };

let cached: MongooseCache = globalWithMongoose.mongoose;

if (!cached) {
    cached = globalWithMongoose.mongoose = { conn: null, promise: null };
}

let gridFsBucket: GridFSBucket;

async function connectToDatabase(): Promise<mongoose.Connection> {
    if (cached.conn) {
        console.log('Using existing database connection');
        return cached.conn;
    }

    if (!cached.promise) {
        if (!URI) {
            throw new Error("MONGODB_URI is not defined");
        }
        cached.promise = mongoose
            .connect(URI as string)
            .then((mongoose) => {
                console.log("New database connection established");
                const db = mongoose.connection.db;
                console.log('Database:', db);
                if (!db) {
                    throw new Error("Database connection failed");
                }
                gridFsBucket = new GridFSBucket(db, { bucketName: "documents" });
                console.log('GridFS Bucket created');
                return mongoose.connection;
            })
            .catch((error) => {
                console.error("Error connecting to database:", error);
                throw error;
            });
    }

    cached.conn = await cached.promise;
    console.log('Database connection:', cached.conn);
    return cached.conn;
}

export { connectToDatabase, gridFsBucket };