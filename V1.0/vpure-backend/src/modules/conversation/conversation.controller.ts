import { FastifyReply, FastifyRequest } from 'fastify';
import { db } from '../../db/client';
import { fail, success } from '../../utils/reply';
import { and, eq, or } from 'drizzle-orm';
import { conversations, messages, users } from '../../db/schema';

import { sql } from 'drizzle-orm';

// Gets all conversations for the logged-in user with aggregated data
export async function getConversationsController(
  request: FastifyRequest,
  reply: FastifyReply
) {
  const userId = request.user.id;

  try {
    const results = await db.execute(sql`
      SELECT
        c.id as "conversationId",
        CASE
          WHEN c.user_a_id = ${userId} THEN u_b.id
          ELSE u_a.id
        END as "otherUserId",
        CASE
          WHEN c.user_a_id = ${userId} THEN u_b.name
          ELSE u_a.name
        END as "otherUserName",
        m.content as "lastMessage",
        m.created_at as "lastMessageAt"
      FROM conversations c
      JOIN users u_a ON c.user_a_id = u_a.id
      JOIN users u_b ON c.user_b_id = u_b.id
      LEFT JOIN (
        SELECT
          conversation_id,
          content,
          created_at,
          ROW_NUMBER() OVER(PARTITION BY conversation_id ORDER BY created_at DESC) as rn
        FROM messages
      ) m ON c.id = m.conversation_id AND m.rn = 1
      WHERE c.user_a_id = ${userId} OR c.user_b_id = ${userId}
      ORDER BY m.created_at DESC;
    `);

    return success(reply, results);
  } catch (error) {
    console.error(error);
    return fail(reply, 'Could not fetch conversations', 500);
  }
}

// Gets all messages for a specific conversation
export async function getConversationMessagesController(
  request: FastifyRequest<{ Params: { id: string } }>,
  reply: FastifyReply
) {
  const conversationId = request.params.id;
  const userId = request.user.id;

  try {
    // First, verify the user is part of this conversation
    const conversation = await db.query.conversations.findFirst({
        where: and(
            eq(conversations.id, conversationId),
            or(
                eq(conversations.userAId, userId),
                eq(conversations.userBId, userId)
            )
        )
    });

    if (!conversation) {
        return fail(reply, 'Conversation not found or you do not have access.', 404);
    }

    const messageList = await db.select().from(messages).where(
        eq(messages.conversationId, conversationId)
    ).orderBy(messages.createdAt);

    return success(reply, messageList);
  } catch (error) {
    console.error(error);
    return fail(reply, 'Could not fetch messages', 500);
  }
}
