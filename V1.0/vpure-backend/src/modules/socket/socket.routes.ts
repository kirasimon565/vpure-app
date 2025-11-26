import { FastifyInstance } from 'fastify';
import { verifyToken } from '../../utils/jwt';
import { clients } from '../../server';
import { users } from '../../db/schema';

interface AuthPayload {
    id: string;
    email: string;
    name: string;
    iat: number;
    exp: number;
}

export async function socketRoutes(app: FastifyInstance) {
    app.get('/ws', { websocket: true }, (connection, req) => {
        const token = req.query.token as string;

        if (!token) {
            connection.socket.send(JSON.stringify({ error: 'Authentication token not provided.' }));
            connection.socket.close(1008, 'Authentication token not provided.');
            return;
        }

        const user = verifyToken<AuthPayload>(token);

        if (!user) {
            connection.socket.send(JSON.stringify({ error: 'Invalid or expired token.' }));
            connection.socket.close(1008, 'Invalid or expired token.');
            return;
        }

        // Store the client connection
        clients.set(user.id, connection);
        console.log(`Client connected: ${user.name} (${user.id})`);

        connection.socket.on('message', message => {
            // Here you could handle incoming messages from the client if needed
            // For now, we are just broadcasting server-sent messages
            console.log(`Received message from ${user.name}: ${message.toString()}`);
        });

        connection.socket.on('close', () => {
            clients.delete(user.id);
            console.log(`Client disconnected: ${user.name} (${user.id})`);
        });

        connection.socket.send(JSON.stringify({ message: 'Connection successful.' }));
    });
}
