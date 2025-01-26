import mongoose from 'mongoose';
import { MongoMemoryServer } from 'mongodb-memory-server';
import { connectToDatabase, gridFsBucket } from '../db';

let mongoServer: MongoMemoryServer;

beforeAll(async () => {
    mongoServer = await MongoMemoryServer.create();
    const mongoUri = mongoServer.getUri();
    process.env.MONGODB_URI = mongoUri;
});

afterAll(async () => {
    await mongoose.disconnect();
    await mongoServer.stop();
});

describe('Database Connection', () => {
    it('should connect to the database', async () => {
        const connection = await connectToDatabase();
        expect(connection).toBeDefined();
        expect(connection.readyState).toBe(1); // 1 means connected
    });

    it('should reuse existing connection', async () => {
        const connection1 = await connectToDatabase();
        const connection2 = await connectToDatabase();
        expect(connection1).toBe(connection2);
    });

    it('should create GridFSBucket', async () => {
        await connectToDatabase();
        expect(gridFsBucket).toBeDefined();
    });

    it('should throw error if MONGODB_URI is not defined', async () => {
        const originalUri = process.env.MONGODB_URI;
        delete process.env.MONGODB_URI;

        await expect(connectToDatabase()).rejects.toThrow('MONGODB_URI is not defined');

        process.env.MONGODB_URI = originalUri;
    });
});