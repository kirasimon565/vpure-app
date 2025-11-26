import fastify, { FastifyReply, FastifyRequest } from 'fastify';
import cors from '@fastify/cors';
import websocket from '@fastify/websocket';
import jwt from '@fastify/jwt';
import { ZodError } from 'zod';
import { fail } from './utils/reply';
import { env } from './env';

import { authRoutes } from './modules/auth/auth.routes';
import { conversationRoutes } from './modules/conversation/conversation.routes';
import { messageRoutes } from './modules/message/message.routes';
import { socketRoutes } from './modules/socket/socket.routes';

export const clients = new Map<string, any>();

export function createServer() {
    const app = fastify({ logger: true });

    // --- PLUGINS ---
    app.register(cors, {
        origin: '*', // For development only
        methods: ['GET', 'POST', 'PUT', 'DELETE'],
    });
    app.register(websocket);
    app.register(jwt, {
        secret: env.JWT_SECRET,
    });

    // --- DECORATORS (for auth) ---
    app.decorate('authenticate', async function (request: FastifyRequest, reply: FastifyReply) {
        try {
            await request.jwtVerify();
        } catch (err) {
            fail(reply, 'Unauthorized', 401);
        }
    });

    // --- ROUTES ---
    app.register(authRoutes, { prefix: '/auth' });
    app.register(conversationRoutes, { prefix: '/conversations' });
    app.register(messageRoutes, { prefix: '/messages' });
    app.register(socketRoutes);

    // --- GLOBAL ERROR HANDLER ---
    app.setErrorHandler((error, _, reply) => {
        if (error instanceof ZodError) {
            return fail(reply, error.flatten().fieldErrors, 400);
        }
        app.log.error(error);
        return fail(reply, 'Internal Server Error', 500);
    });

    return app;
}
