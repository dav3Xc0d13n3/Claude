package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ProviderType {
    NVIDIA_NIM,
    GEMINI,
    OPENAI,
    ANTHROPIC,
    GROQ,
    OPENROUTER,
    CUSTOM
}

@Entity(tableName = "ai_providers")
data class AiProviderEntity(
    @PrimaryKey val id: String, // e.g. "nvidia", "gemini", "openai"
    val name: String,
    val type: ProviderType,
    val apiKey: String,
    val baseUrl: String = "",
    val isEnabled: Boolean = true,
    val customModelName: String = ""
)

@Entity(tableName = "workspaces")
data class WorkspaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconName: String = "Folder",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val workspaceId: String,
    val name: String,
    val description: String,
    val colorHex: String = "#DA7756",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val workspaceId: String,
    val projectId: String? = null,
    val title: String,
    val modelId: String,
    val providerType: ProviderType,
    val folderName: String = "",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER, ASSISTANT, SYSTEM, AGENT
}

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val sender: MessageSender,
    val content: String,
    val thinkingContent: String = "",
    val modelUsed: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrisJson: String = "[]",
    val attachedFileUrisJson: String = "[]",
    val isStarred: Boolean = false,
    val parentMessageId: String? = null
)

@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey val id: String,
    val projectId: String? = null,
    val name: String,
    val description: String,
    val instructions: String,
    val triggerConditions: String = "",
    val enabledToolsJson: String = "[]",
    val isAutoTrigger: Boolean = true
)

@Entity(tableName = "plugins")
data class PluginEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val description: String,
    val iconName: String,
    val isInstalled: Boolean = false,
    val isEnabled: Boolean = false,
    val configJson: String = "{}"
)

@Entity(tableName = "custom_agents")
data class CustomAgentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatarUrl: String = "",
    val description: String,
    val systemPrompt: String,
    val modelId: String = "gemini-3.5-flash",
    val providerType: ProviderType = ProviderType.GEMINI,
    val assignedSkillsJson: String = "[]",
    val toolsJson: String = "[]"
)

@Entity(tableName = "knowledge_docs")
data class KnowledgeDocEntity(
    @PrimaryKey val id: String,
    val workspaceId: String,
    val projectId: String? = null,
    val title: String,
    val content: String,
    val fileType: String = "TXT",
    val wordCount: Int = 0,
    val chunkCount: Int = 0,
    val uploadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "github_repos")
data class GithubRepoEntity(
    @PrimaryKey val id: String,
    val name: String,
    val owner: String,
    val url: String,
    val defaultBranch: String = "main",
    val description: String = "",
    val language: String = "Kotlin",
    val stars: Int = 0,
    val forks: Int = 0,
    val isConnected: Boolean = true
)

// Data DTOs for model discovery
data class ModelInfo(
    val id: String,
    val name: String,
    val providerType: ProviderType,
    val providerName: String,
    val description: String,
    val contextWindow: String,
    val supportsVision: Boolean = false,
    val supportsReasoning: Boolean = false,
    val supportsAudio: Boolean = false,
    val pricingInfo: String = "Free / Standard Tier"
)

enum class MemoryType {
    LONG_TERM,
    PROJECT,
    USER_PREFERENCE
}

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val workspaceId: String = "ws-default-001",
    val projectId: String? = null,
    val title: String,
    val content: String,
    val type: MemoryType = MemoryType.LONG_TERM,
    val isPinned: Boolean = false,
    val category: String = "General",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "generated_images")
data class GeneratedImageEntity(
    @PrimaryKey val id: String,
    val prompt: String,
    val aspectRatio: String = "1:1",
    val imageUrl: String,
    val modelUsed: String = "Gemini 2.5 Flash Image",
    val styleCategory: String = "Futuristic AI",
    val timestamp: Long = System.currentTimeMillis()
)
