import { pgTable, text, timestamp, uuid, varchar } from 'drizzle-orm/pg-core';
import { relations } from 'drizzle-orm';

// ----------------------------
// USERS
// ----------------------------
export const users = pgTable('users', {
    id: uuid('id').primaryKey().defaultRandom(),
    name: varchar('name', { length: 100 }).notNull(),
    email: varchar('email', { length: 255 }).notNull().unique(),
    passwordHash: text('password_hash').notNull(),
    createdAt: timestamp('created_at').defaultNow().notNull(),
});

// ----------------------------
// CONVERSATIONS
// ----------------------------
export const conversations = pgTable('conversations', {
    id: uuid('id').primaryKey().defaultRandom(),
    userAId: uuid('user_a_id')
        .notNull()
        .references(() => users.id, { onDelete: 'cascade' }),
    userBId: uuid('user_b_id')
        .notNull()
        .references(() => users.id, { onDelete: 'cascade' }),
    createdAt: timestamp('created_at').defaultNow().notNull(),
});

// ----------------------------
// MESSAGES
// ----------------------------
export const messages = pgTable('messages', {
    id: uuid('id').primaryKey().defaultRandom(),
    conversationId: uuid('conversation_id')
        .notNull()
        .references(() => conversations.id, { onDelete: 'cascade' }),
    senderId: uuid('sender_id')
        .notNull()
        .references(() => users.id, { onDelete: 'cascade' }),
    content: text('content').notNull(),
    createdAt: timestamp('created_at').defaultNow().notNull(),
});

// ----------------------------
// RELATIONS
// ----------------------------

export const usersRelations = relations(users, ({ many }) => ({
    conversationsA: many(conversations, { relationName: 'userA' }),
    conversationsB: many(conversations, { relationName: 'userB' }),
    messages: many(messages),
}));

export const conversationsRelations = relations(conversations, ({ one, many }) => ({
    userA: one(users, {
        fields: [conversations.userAId],
        references: [users.id],
        relationName: 'userA',
    }),
    userB: one(users, {
        fields: [conversations.userBId],
        references: [users.id],
        relationName: 'userB',
    }),
    messages: many(messages),
}));

export const messagesRelations = relations(messages, ({ one }) => ({
    conversation: one(conversations, {
        fields: [messages.conversationId],
        references: [conversations.id],
    }),
    sender: one(users, {
        fields: [messages.senderId],
        references: [users.id],
    }),
}));
