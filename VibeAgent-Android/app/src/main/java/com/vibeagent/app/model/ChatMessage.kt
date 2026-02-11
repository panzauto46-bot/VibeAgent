package com.vibeagent.app.model

import java.util.Date

data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Date = Date(),
    val type: MessageType = MessageType.TEXT,
    val txHash: String? = null
)

enum class MessageRole {
    USER,
    AI
}

enum class MessageType {
    TEXT,
    WALLET,
    BALANCE,
    TRANSACTION,
    ERROR
}
