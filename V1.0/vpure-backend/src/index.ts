import { env } from './env';
import { createServer } from './server';

async function startServer() {
    const app = createServer();
    try {
        await app.listen({ port: env.PORT, host: '0.0.0.0' });
        console.log(`Server listening on port ${env.PORT}`);
    } catch (err) {
        app.log.error(err);
        process.exit(1);
    }
}

startServer();
