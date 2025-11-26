package com.vpure.app.api

import com.vpure.app.models.ConversationInfo
import com.vpure.app.models.Message
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class SendMessageRequest(val conversationId: String, val content: String)

interface ChatService {
    @GET("conversations")
    suspend fun getConversations(): Response<List<ConversationInfo>>

    @GET("conversations/{id}/messages")
    suspend fun getMessages(@Path("id") conversationId: String): Response<List<Message>>

    @POST("messages/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<Message>
}
