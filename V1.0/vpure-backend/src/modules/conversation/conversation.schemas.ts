import { z } from 'zod';
import { zodToJsonSchema } from 'zod-to-json-schema';

const getConversationMessagesSchema = z.object({
  id: z.string().uuid(),
});

export const getConversationMessagesJsonSchema = {
  params: zodToJsonSchema(getConversationMessagesSchema, 'getConversationMessagesSchema'),
};
