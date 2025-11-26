package com.vpure.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vpure.app.databinding.ItemConversationBinding
import com.vpure.app.models.ConversationInfo
import com.vpure.app.utils.DateUtils

class ConversationAdapter(
    private var conversations: List<ConversationInfo>,
    private val onItemClick: (ConversationInfo) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount(): Int = conversations.size

    fun updateData(newConversations: List<ConversationInfo>) {
        conversations = newConversations
        notifyDataSetChanged()
    }

    inner class ConversationViewHolder(private val binding: ItemConversationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(conversation: ConversationInfo) {
            binding.username.text = conversation.otherUserName
            binding.lastMessage.text = conversation.lastMessage ?: "No messages yet"
            binding.timestamp.text = conversation.lastMessageAt?.let { DateUtils.formatTimestamp(it) } ?: ""

            itemView.setOnClickListener {
                onItemClick(conversation)
            }
        }
    }
}
