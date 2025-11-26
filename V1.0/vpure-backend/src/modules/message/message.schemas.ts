import { z } from 'zod';
import { zodToJsonSchema } from 'zod-to-json-schema';

const sendMessageSchema = z.object({
  conversationId: z.string().uuid(),
  content: z.string().min(1, 'Message cannot be empty'),
});

export type SendMessageInput = z.infer<typeof sendMessageSchema>;
export const sendMessageJsonSchema = {
  body: zodToJsonSchema(sendMessageSchema, 'sendMessageSchema'),
};
