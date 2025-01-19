import { createClient, RedisClientType } from 'redis';

const client : RedisClientType = createClient({
    password: process.env.REDIS_PW,
    socket: {
        host: process.env.REDIS_HOST,
        port: process.env.REDIS_PORT ? parseInt(process.env.REDIS_PORT) : undefined
    }
});

client.on('error', (err: Error) => {
    console.log('Redis connection Error!', err);
});

(async () => {
    if(!client.isOpen){
        await client.connect();
    }
})();

export default client;