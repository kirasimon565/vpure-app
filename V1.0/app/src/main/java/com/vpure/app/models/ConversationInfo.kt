package com.vpure.app.models

import java.util.Date

data class ConversationInfo(
    val conversationId: String,
    val otherUserId: String,
    val otherUserName: String,
    val lastMessage: String?,
    val lastMessageAt: Date?
)
