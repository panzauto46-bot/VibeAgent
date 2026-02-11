package com.vibeagent.app.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vibeagent.app.databinding.ItemChatMessageBinding
import com.vibeagent.app.model.ChatMessage
import com.vibeagent.app.model.MessageRole

class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.ChatViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChatViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            when (message.role) {
                MessageRole.AI -> {
                    binding.bubbleAi.visibility = View.VISIBLE
                    binding.bubbleUser.visibility = View.GONE
                    binding.textAiMessage.text = formatMessage(message.content)
                }
                MessageRole.USER -> {
                    binding.bubbleAi.visibility = View.GONE
                    binding.bubbleUser.visibility = View.VISIBLE
                    binding.textUserMessage.text = message.content
                }
            }
        }

        private fun formatMessage(content: String): CharSequence {
            // Convert markdown-like formatting to simple display
            val formatted = content
                .replace("**", "")
                .replace("\\n", "\n")
                .replace("`", "")
            return formatted
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
