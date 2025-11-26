import { FastifyReply, FastifyRequest } from 'fastify';
import { and, eq, or } from 'drizzle-orm';
import { db } from '../../db/client';
import { conversations, messages } from '../../db/schema';
import { fail, success } from '../../utils/reply';
import { SendMessageInput } from './message.schemas';
import { clients } from '../../server';
import WebSocket from 'ws';

export async function sendMessageController(
  request: FastifyRequest<{ Body: SendMessageInput }>,
  reply: FastifyReply
) {
  const { conversationId, content } = request.body;
  const senderId = request.user.id;

  try {
    // 1. Verify the user is part of the conversation they're trying to send a message to
    const conversation = await db.query.conversations.findFirst({
      where: and(
        eq(conversations.id, conversationId),
        or(
          eq(conversations.userAId, senderId),
          eq(conversations.userBId, senderId)
        )
      )
    });

    if (!conversation) {
      return fail(reply, 'Conversation not found or you do not have access.', 404);
    }

    // 2. Insert the new message
    const [newMessage] = await db.insert(messages).values({
      conversationId,
      senderId,
      content,
    }).returning();

    // --- WebSocket Real-time Broadcasting ---
    const recipientId = conversation.userAId === senderId ? conversation.userBId : conversation.userAId;
    const recipientSocket = clients.get(recipientId);

    if (recipientSocket && recipientSocket.socket.readyState === WebSocket.OPEN) {
      try {
        recipientSocket.socket.send(JSON.stringify({ type: 'NEW_MESSAGE', payload: newMessage }));
      } catch (wsError) {
        console.error('Failed to send message via WebSocket:', wsError);
      }
    }
    // --- End WebSocket Logic ---

    return success(reply, newMessage, 201);

  } catch (error) {
    console.error(error);
    return fail(reply, 'Could not send message', 500);
  }
}
