import { FastifyInstance } from 'fastify';
import { getConversationMessagesController, getConversationsController } from './conversation.controller';
import { getConversationMessagesJsonSchema } from './conversation.schemas';

export async function conversationRoutes(app: FastifyInstance) {
  // All routes in this file require authentication
  app.addHook('onRequest', app.authenticate);

  app.get(
    '/',
    getConversationsController
  );

  app.get(
    '/:id/messages',
    { schema: getConversationMessagesJsonSchema },
    getConversationMessagesController
  );
}
