import { FastifyInstance } from 'fastify';
import { User } from './db/schema';

declare module 'fastify' {
  export interface FastifyInstance {
    authenticate: (request: FastifyRequest, reply: FastifyReply) => Promise<void>;
  }
  export interface FastifyRequest {
    user: User;
  }
}
