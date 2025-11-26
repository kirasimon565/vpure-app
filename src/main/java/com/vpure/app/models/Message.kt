package com.vpure.app.models

import java.util.Date

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val createdAt: Date
)
