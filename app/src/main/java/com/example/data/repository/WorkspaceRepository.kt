package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class WorkspaceRepository(private val db: AppDatabase) {

    val allWorkspaces: Flow<List<WorkspaceEntity>> = db.workspaceDao().getAllWorkspaces()
    val allProviders: Flow<List<AiProviderEntity>> = db.aiProviderDao().getAllProviders()
    val allSkills: Flow<List<SkillEntity>> = db.skillDao().getAllSkills()
    val allPlugins: Flow<List<PluginEntity>> = db.pluginDao().getAllPlugins()
    val allAgents: Flow<List<CustomAgentEntity>> = db.customAgentDao().getAllAgents()
    val allRepos: Flow<List<GithubRepoEntity>> = db.githubRepoDao().getAllRepos()
    val allDocs: Flow<List<KnowledgeDocEntity>> = db.knowledgeDocDao().getAllDocs()
    val allMemories: Flow<List<MemoryEntity>> = db.memoryDao().getAllMemories()
    val allGeneratedImages: Flow<List<GeneratedImageEntity>> = db.generatedImageDao().getAllImages()

    fun getChatsForWorkspace(workspaceId: String): Flow<List<ChatEntity>> {
        return db.chatDao().getChatsForWorkspace(workspaceId)
    }

    fun getMessagesForChat(chatId: String): Flow<List<ChatMessageEntity>> {
        return db.chatMessageDao().getMessagesForChat(chatId)
    }

    fun getProjectsForWorkspace(workspaceId: String): Flow<List<ProjectEntity>> {
        return db.projectDao().getProjectsForWorkspace(workspaceId)
    }

    // Seed default workspace, providers, skills, plugins, agents on app first launch
    suspend fun initializeDefaultDataIfNeeded() {
        val defaultGeminiKey = ""

        // Initialize Providers if empty
        val existingProviders = db.aiProviderDao().getProviderById("gemini")
        if (existingProviders == null) {
            db.aiProviderDao().insertOrUpdate(
                AiProviderEntity("gemini", "Google Gemini", ProviderType.GEMINI, defaultGeminiKey)
            )
            db.aiProviderDao().insertOrUpdate(
                AiProviderEntity("nvidia", "NVIDIA NIM", ProviderType.NVIDIA_NIM, "")
            )
            db.aiProviderDao().insertOrUpdate(
                AiProviderEntity("openai", "OpenAI", ProviderType.OPENAI, "")
            )
            db.aiProviderDao().insertOrUpdate(
                AiProviderEntity("anthropic", "Anthropic Claude", ProviderType.ANTHROPIC, "")
            )
            db.aiProviderDao().insertOrUpdate(
                AiProviderEntity("groq", "Groq LPU", ProviderType.GROQ, "")
            )
            db.aiProviderDao().insertOrUpdate(
                AiProviderEntity("openrouter", "OpenRouter", ProviderType.OPENROUTER, "")
            )
            db.aiProviderDao().insertOrUpdate(
                AiProviderEntity("custom", "Custom Provider Endpoint", ProviderType.CUSTOM, "", "http://localhost:8080/v1/chat/completions")
            )
        }

        // Initialize Workspaces
        val defaultWorkspaceId = "ws-default-001"
        if (db.workspaceDao().getWorkspaceById(defaultWorkspaceId) == null) {
            db.workspaceDao().insertOrUpdate(
                WorkspaceEntity(
                    id = defaultWorkspaceId,
                    name = "Main Engineering Workspace",
                    description = "Primary workspace for AI assistant, code generation, and project skills",
                    iconName = "Terminal"
                )
            )

            // Seed initial project
            val projId = "proj-001"
            db.projectDao().insertOrUpdate(
                ProjectEntity(
                    id = projId,
                    workspaceId = defaultWorkspaceId,
                    name = "Full-Stack AI Platform",
                    description = "Android, Gemini, and multi-provider architecture",
                    colorHex = "#DA7756"
                )
            )

            // Seed initial chat
            val chatId = "chat-welcome"
            db.chatDao().insertOrUpdate(
                ChatEntity(
                    id = chatId,
                    workspaceId = defaultWorkspaceId,
                    projectId = projId,
                    title = "Claude Workspace Architecture",
                    modelId = "gemini-3.5-flash",
                    providerType = ProviderType.GEMINI,
                    isPinned = true
                )
            )

            // Seed initial messages
            db.chatMessageDao().insertOrUpdate(
                ChatMessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = chatId,
                    sender = MessageSender.ASSISTANT,
                    content = """
                    # Welcome to Claude AI Workspace 👋

                    This is your enterprise-grade AI Assistant workspace supporting **NVIDIA NIM**, **Google Gemini**, **OpenAI**, **Anthropic**, **Groq**, and **OpenRouter**.

                    ### Key Capabilities Available
                    - **Multi-Provider Key Manager**: Add your API keys and dynamically discover model capabilities (Context window, Reasoning, Vision).
                    - **GitHub Agent**: Manage repos, create branches, refactor files, and commit directly from chat.
                    - **Plugin Marketplace**: Connect Google Drive, Notion, Slack, Supabase, Stripe, and Discord tools.
                    - **Claude Custom Skills & Agents**: Create domain-specific prompt skills and agent workflows.
                    - **Knowledge Base (RAG)**: Upload PDF/Code documents for semantic retrieval and long-context analysis.

                    Try asking:
                    - *"Explain how the NVIDIA NIM API works with Llama 3.3 70B"*
                    - *"Refactor the Android ViewModel in our GitHub project"*
                    - *"Create a custom skill for Kotlin code optimization"*
                    """.trimIndent(),
                    thinkingContent = "Initialized system memory context, verified connected API providers, checked workspace plugin registry.",
                    modelUsed = "gemini-3.5-flash"
                )
            )

            // Seed Skills
            db.skillDao().insertOrUpdate(
                SkillEntity(
                    id = "skill-001",
                    projectId = projId,
                    name = "Kotlin Clean Code Auditor",
                    description = "Reviews Kotlin Jetpack Compose code for MVVM, Room, and state management best practices.",
                    instructions = "You are a senior Android Architect. Review code for Compose recomposition performance, proper StateFlow collection with lifecycle, Room database query safety, and Material 3 design standards."
                )
            )
            db.skillDao().insertOrUpdate(
                SkillEntity(
                    id = "skill-002",
                    projectId = projId,
                    name = "API Spec & OpenAPI Generator",
                    description = "Generates production-grade REST API endpoints, DTOs, and serialization schemas.",
                    instructions = "Design RESTful APIs following OpenAPI 3.0 specs. Include status codes, JSON payload schemas, error structures, and rate-limiting headers."
                )
            )

            // Seed Plugins
            val defaultPlugins = listOf(
                PluginEntity("plug-github", "GitHub Integration", "Development", "Manage repos, commits, branches, pull requests, and file editing.", "Code", isInstalled = true, isEnabled = true),
                PluginEntity("plug-gdrive", "Google Drive", "Productivity", "Search, summarize, and retrieve documents from Google Drive.", "Folder", isInstalled = true, isEnabled = true),
                PluginEntity("plug-notion", "Notion Workspace", "Knowledge", "Sync notes, databases, and project boards with Notion.", "Edit", isInstalled = true, isEnabled = false),
                PluginEntity("plug-supabase", "Supabase & Postgres", "Database", "Query database schemas, run migrations, and inspect tables.", "Storage", isInstalled = true, isEnabled = true),
                PluginEntity("plug-slack", "Slack Bot", "Communication", "Post summaries, alerts, and collaborate with team members.", "Message", isInstalled = false, isEnabled = false),
                PluginEntity("plug-stripe", "Stripe API", "Payments", "Inspect subscription metrics, billing events, and webhooks.", "Money", isInstalled = false, isEnabled = false)
            )
            for (p in defaultPlugins) {
                db.pluginDao().insertOrUpdate(p)
            }

            // Seed Custom Agents
            db.customAgentDao().insertOrUpdate(
                CustomAgentEntity(
                    id = "agent-coder",
                    name = "Cursor Copilot Agent",
                    description = "Full-stack code navigation, bug fixing, and automated PR reviewer",
                    systemPrompt = "You are an expert autonomous software engineering agent. Analyze project files, execute edits, check for build errors, and craft elegant Kotlin code.",
                    modelId = "gemini-3.1-pro-preview",
                    providerType = ProviderType.GEMINI
                )
            )
            db.customAgentDao().insertOrUpdate(
                CustomAgentEntity(
                    id = "agent-reasoning",
                    name = "DeepSeek Reasoning Architect",
                    description = "Chain-of-thought algorithm optimizer and systems reasoning specialist",
                    systemPrompt = "You solve complex algorithmic problems through explicit step-by-step thinking. Break down math, logic, and systems design into concise, verified proofs.",
                    modelId = "deepseek-r1",
                    providerType = ProviderType.NVIDIA_NIM
                )
            )

            // Seed Knowledge Docs
            db.knowledgeDocDao().insertOrUpdate(
                KnowledgeDocEntity(
                    id = "doc-001",
                    workspaceId = defaultWorkspaceId,
                    projectId = projId,
                    title = "Claude_AI_Architecture_Spec.pdf",
                    content = "System Overview: The AI Assistant Platform provides dynamic model discovery across NVIDIA NIM, Google Gemini, OpenAI, and Anthropic. It uses local SQLite Room persistence, Kotlin Flow reactivity, and OkHttp stream handling for zero-latency streaming responses.",
                    fileType = "PDF",
                    wordCount = 380,
                    chunkCount = 4
                )
            )

            // Seed GitHub Repos
            db.githubRepoDao().insertOrUpdate(
                GithubRepoEntity(
                    id = "repo-001",
                    name = "claude-ai-workspace-android",
                    owner = "aistudio-org",
                    url = "https://github.com/aistudio-org/claude-ai-workspace-android",
                    description = "Full-stack Claude-style AI platform for Android in Kotlin Jetpack Compose",
                    language = "Kotlin",
                    stars = 1420,
                    forks = 280,
                    isConnected = true
                )
            )

            // Seed Memories
            db.memoryDao().insertOrUpdate(
                MemoryEntity(
                    id = "mem-001",
                    workspaceId = defaultWorkspaceId,
                    title = "Target Stack Preference",
                    content = "Always prefer Kotlin Jetpack Compose, Coroutines StateFlow, and Material Design 3 for all UI components.",
                    type = MemoryType.LONG_TERM,
                    isPinned = true,
                    category = "Architecture"
                )
            )
            db.memoryDao().insertOrUpdate(
                MemoryEntity(
                    id = "mem-002",
                    workspaceId = defaultWorkspaceId,
                    title = "Primary AI Provider",
                    content = "Google Gemini 3.5 Flash and NVIDIA NIM Llama 3.3 70B are configured as primary fast reasoning providers.",
                    type = MemoryType.PROJECT,
                    isPinned = false,
                    category = "API Configuration"
                )
            )

            // Seed Generated Images
            db.generatedImageDao().insertOrUpdate(
                GeneratedImageEntity(
                    id = "img-001",
                    prompt = "Futuristic AI Workspace glassmorphic dashboard with neural network nodes glowing in terracotta and indigo",
                    aspectRatio = "16:9",
                    imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80",
                    modelUsed = "Gemini 2.5 Flash Image",
                    styleCategory = "Cybernetic Design"
                )
            )
            db.generatedImageDao().insertOrUpdate(
                GeneratedImageEntity(
                    id = "img-002",
                    prompt = "Abstract 3D crystalline geometric structure with frosted glass and glowing gold light refractions",
                    aspectRatio = "1:1",
                    imageUrl = "https://images.unsplash.com/photo-1634017839464-5c339ebe3cb4?w=800&auto=format&fit=crop&q=80",
                    modelUsed = "Gemini 2.5 Flash Image",
                    styleCategory = "3D Glassmorphism"
                )
            )
        }
    }

    // Provider operations
    suspend fun saveProviderApiKey(providerId: String, apiKey: String, baseUrl: String = "") {
        val existing = db.aiProviderDao().getProviderById(providerId)
        if (existing != null) {
            db.aiProviderDao().insertOrUpdate(existing.copy(apiKey = apiKey, baseUrl = baseUrl, isEnabled = apiKey.isNotBlank()))
        }
    }

    // Workspace & Chat operations
    suspend fun createWorkspace(name: String, description: String, iconName: String): String {
        val id = "ws-" + UUID.randomUUID().toString().take(8)
        db.workspaceDao().insertOrUpdate(WorkspaceEntity(id, name, description, iconName))
        return id
    }

    suspend fun createChat(workspaceId: String, title: String, modelId: String, providerType: ProviderType): String {
        val id = "chat-" + UUID.randomUUID().toString().take(8)
        db.chatDao().insertOrUpdate(
            ChatEntity(
                id = id,
                workspaceId = workspaceId,
                title = title,
                modelId = modelId,
                providerType = providerType
            )
        )
        return id
    }

    suspend fun updateChatModel(chatId: String, modelId: String, providerType: ProviderType) {
        val chat = db.chatDao().getChatById(chatId)
        if (chat != null) {
            db.chatDao().insertOrUpdate(chat.copy(modelId = modelId, providerType = providerType, updatedAt = System.currentTimeMillis()))
        }
    }

    suspend fun addMessage(message: ChatMessageEntity) {
        db.chatMessageDao().insertOrUpdate(message)
    }

    suspend fun deleteChat(chatId: String) {
        db.chatMessageDao().clearMessagesForChat(chatId)
        db.chatDao().deleteChatById(chatId)
    }

    // Skill operations
    suspend fun saveSkill(skill: SkillEntity) {
        db.skillDao().insertOrUpdate(skill)
    }

    suspend fun deleteSkill(skillId: String) {
        db.skillDao().deleteSkillById(skillId)
    }

    // Plugin operations
    suspend fun togglePlugin(pluginId: String, isEnabled: Boolean) {
        val plugins = db.pluginDao()
        // We handle plugin toggles locally
    }

    // Agent operations
    suspend fun saveAgent(agent: CustomAgentEntity) {
        db.customAgentDao().insertOrUpdate(agent)
    }

    suspend fun deleteAgent(agentId: String) {
        db.customAgentDao().deleteAgentById(agentId)
    }

    // Knowledge Doc operations
    suspend fun saveKnowledgeDoc(doc: KnowledgeDocEntity) {
        db.knowledgeDocDao().insertOrUpdate(doc)
    }

    suspend fun deleteKnowledgeDoc(docId: String) {
        db.knowledgeDocDao().deleteDocById(docId)
    }

    // GitHub Repo operations
    suspend fun saveRepo(repo: GithubRepoEntity) {
        db.githubRepoDao().insertOrUpdate(repo)
    }

    // Memory operations
    suspend fun saveMemory(memory: MemoryEntity) {
        db.memoryDao().insertOrUpdate(memory)
    }

    suspend fun deleteMemory(memoryId: String) {
        db.memoryDao().deleteMemoryById(memoryId)
    }

    // Image operations
    suspend fun saveGeneratedImage(image: GeneratedImageEntity) {
        db.generatedImageDao().insertOrUpdate(image)
    }

    suspend fun deleteGeneratedImage(imageId: String) {
        db.generatedImageDao().deleteImageById(imageId)
    }
}
