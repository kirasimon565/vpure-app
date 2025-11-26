package com.vpure.app.models

import java.util.Date

data class Conversation(
    val id: String,
    val userAId: String,
    val userBId: String,
    val createdAt: Date
    // Note: The backend's GET /conversations endpoint sends a more detailed object.
    // We will handle that aggregation in the HomeActivity for simplicity.
)
