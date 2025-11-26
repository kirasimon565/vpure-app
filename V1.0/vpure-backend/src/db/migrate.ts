import { drizzle } from 'drizzle-orm/node-postgres';
import { migrate } from 'drizzle-orm/node-postgres/migrator';
import { Pool } from 'pg';
import { env } from '../env';

async function runMigrations() {
    console.log('Connecting to database...');
    const pool = new Pool({
        connectionString: env.DATABASE_URL,
    });

    const db = drizzle(pool);

    console.log('Running database migrations...');
    try {
        await migrate(db, { migrationsFolder: './src/db/migrations' });
        console.log('Migrations completed successfully!');
    } catch (error) {
        console.error('Error running migrations:', error);
        process.exit(1);
    } finally {
        await pool.end();
        console.log('Database connection closed.');
    }
}

runMigrations();
