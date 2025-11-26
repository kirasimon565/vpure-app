import { FastifyInstance } from 'fastify';
import { sendMessageController } from './message.controller';
import { sendMessageJsonSchema } from './message.schemas';

export async function messageRoutes(app: FastifyInstance) {
  // All routes in this file require authentication
  app.addHook('onRequest', app.authenticate);

  app.post(
    '/send',
    { schema: sendMessageJsonSchema },
    sendMessageController
  );
}
