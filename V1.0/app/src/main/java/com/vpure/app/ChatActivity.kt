package com.vpure.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vpure.app.adapters.ChatAdapter
import com.vpure.app.api.ApiClient
import com.vpure.app.api.ChatService
import com.vpure.app.api.SendMessageRequest
import com.vpure.app.data.Preferences
import com.vpure.app.data.UserManager
import com.vpure.app.databinding.ActivityChatBinding
import com.vpure.app.models.Message
import com.vpure.app.utils.Constants
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.WebSocket
import com.google.gson.Gson

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private var webSocket: WebSocket? = null
    private val gson = Gson()

    private val chatService: ChatService by lazy {
        ApiClient.getClient(this).create(ChatService::class.java)
    }
    private val okHttpClient: OkHttpClient by lazy {
        ApiClient.getWebSocketClient(this)
    }

    private var conversationId: String? = null
    private var currentUserId: String? = null // This would be fetched from a user profile in a real app

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        conversationId = intent.getStringExtra("CONVERSATION_ID")
        if (conversationId == null) {
            showError("Conversation not found.")
            finish()
            return
        }

        currentUserId = UserManager.getUser(this)?.id
        if (currentUserId == null) {
            showError("User not found. Please log in again.")
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        fetchMessageHistory()
        setupWebSocket()

        binding.sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(mutableListOf(), currentUserId ?: "")
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun fetchMessageHistory() {
        lifecycleScope.launch {
            try {
                val response = chatService.getMessages(conversationId!!)
                if (response.isSuccessful && response.body() != null) {
                    chatAdapter.setMessages(response.body()!!)
                    scrollToBottom()
                } else {
                    showError("Failed to load messages.")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }

    private fun setupWebSocket() {
        val token = Preferences.getToken(this)
        if (token == null) {
            showError("Authentication error. Please log in again.")
            return
        }

        val request = Request.Builder()
            .url("${Constants.WEBSOCKET_URL}?token=$token")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                runOnUiThread {
                    handleSocketMessage(text)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                runOnUiThread {
                    showError("WebSocket connection failed: ${t.message}")
                }
            }
        })
    }

    private fun handleSocketMessage(json: String) {
        try {
            // Assuming the server sends messages like: { "type": "NEW_MESSAGE", "payload": { ...message... } }
            val messageWrapper = gson.fromJson(json, SocketMessage::class.java)
            if (messageWrapper.type == "NEW_MESSAGE") {
                chatAdapter.addMessage(messageWrapper.payload)
                scrollToBottom()
            }
        } catch (e: Exception) {
            // Could be a simple connection confirmation message
            println("Received non-message JSON from socket: $json")
        }
    }

    private fun sendMessage() {
        val content = binding.messageInput.text.toString().trim()
        if (content.isEmpty()) return

        binding.messageInput.text.clear()

        lifecycleScope.launch {
            try {
                val request = SendMessageRequest(conversationId!!, content)
                val response = chatService.sendMessage(request)
                if (response.isSuccessful && response.body() != null) {
                    // Message sent successfully, it will be received via WebSocket by the other user
                    // We can also add it to our own UI immediately for a faster feel
                    chatAdapter.addMessage(response.body()!!)
                    scrollToBottom()
                } else {
                    showError("Failed to send message.")
                    binding.messageInput.setText(content) // Restore text on failure
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
                binding.messageInput.setText(content) // Restore text on failure
            }
        }
    }

    private fun scrollToBottom() {
        binding.messagesRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket?.close(1000, "Activity Destroyed")
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}

// Helper class for parsing WebSocket messages
data class SocketMessage(val type: String, val payload: Message)
