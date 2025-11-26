package com.vpure.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vpure.app.adapters.ConversationAdapter
import com.vpure.app.api.ApiClient
import com.vpure.app.api.ChatService
import com.vpure.app.databinding.ActivityHomeBinding
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var conversationAdapter: ConversationAdapter
    private val chatService: ChatService by lazy {
        ApiClient.getClient(this).create(ChatService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchConversations()
    }

    private fun setupRecyclerView() {
        conversationAdapter = ConversationAdapter(emptyList()) { conversationInfo ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("CONVERSATION_ID", conversationInfo.conversationId)
            startActivity(intent)
        }
        binding.conversationsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = conversationAdapter
        }
    }

    private fun fetchConversations() {
        toggleLoading(true)
        lifecycleScope.launch {
            try {
                val response = chatService.getConversations()
                if (response.isSuccessful && response.body() != null) {
                    val conversations = response.body()!!
                    if (conversations.isEmpty()) {
                        binding.emptyState.visibility = View.VISIBLE
                        binding.conversationsRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyState.visibility = View.GONE
                        binding.conversationsRecyclerView.visibility = View.VISIBLE
                        conversationAdapter.updateData(conversations)
                    }
                } else {
                    showError("Failed to load conversations.")
                }
            } catch (e: Exception) {
                showError("An error occurred: ${e.message}")
            } finally {
                toggleLoading(false)
            }
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
