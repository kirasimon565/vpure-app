import { FastifyReply } from 'fastify';

export function success<T>(reply: FastifyReply, data: T, statusCode = 200) {
    return reply.status(statusCode).send({
        success: true,
        data,
    });
}

export function fail(reply: FastifyReply, message: string | object, statusCode = 400) {
    return reply.status(statusCode).send({
        success: false,
        error: message,
    });
}
