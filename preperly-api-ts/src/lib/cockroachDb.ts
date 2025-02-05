import { Client } from "pg";
import * as dotenv from "dotenv";

dotenv.config({ path: ".env.local" });

const client = new Client(process.env.COCKROACH_DATABASE_URL);

(async () => {
  await client.connect();
  try {
    const results = await client.query("SELECT NOW()");
    console.log(results);
  } catch (err) {
    console.error("error executing query:", err);
  } finally {
    client.end();
  }
})();
