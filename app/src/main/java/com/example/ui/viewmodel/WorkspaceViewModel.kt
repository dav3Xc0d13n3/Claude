package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.AiApiClient
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.WorkspaceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.ui.theme.AppThemeMode
import java.util.Locale
import java.util.UUID

enum class NavigationScreen {
    CHAT,
    WORKSPACES,
    PROVIDERS,
    PLUGINS,
    SKILLS,
    AGENTS,
    GITHUB,
    KNOWLEDGE_BASE,
    PLAYGROUND,
    MEMORY,
    IMAGE_STUDIO,
    VOICE_STUDIO,
    SETTINGS
}

class WorkspaceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = WorkspaceRepository(db)

    // Theme mode state (Dark, Light, Amoled)
    private val _themeMode = MutableStateFlow(AppThemeMode.DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    // Right Sidebar / Context Panel state
    private val _isRightSidebarOpen = MutableStateFlow(false)
    val isRightSidebarOpen: StateFlow<Boolean> = _isRightSidebarOpen.asStateFlow()

    // Command Palette / Spotlight Search modal state
    private val _isCommandPaletteOpen = MutableStateFlow(false)
    val isCommandPaletteOpen: StateFlow<Boolean> = _isCommandPaletteOpen.asStateFlow()

    // API Key modal state
    private val _isApiKeyModalOpen = MutableStateFlow(false)
    val isApiKeyModalOpen: StateFlow<Boolean> = _isApiKeyModalOpen.asStateFlow()

    // Settings modal state
    private val _isSettingsModalOpen = MutableStateFlow(false)
    val isSettingsModalOpen: StateFlow<Boolean> = _isSettingsModalOpen.asStateFlow()

    // Navigation state
    private val _currentScreen = MutableStateFlow(NavigationScreen.CHAT)
    val currentScreen: StateFlow<NavigationScreen> = _currentScreen.asStateFlow()

    // Workspace & Chat Selection State
    private val _selectedWorkspaceId = MutableStateFlow("ws-default-001")
    val selectedWorkspaceId: StateFlow<String> = _selectedWorkspaceId.asStateFlow()

    private val _selectedChatId = MutableStateFlow("chat-welcome")
    val selectedChatId: StateFlow<String> = _selectedChatId.asStateFlow()

    // Model selection state
    private val _selectedModelId = MutableStateFlow("gemini-3.5-flash")
    val selectedModelId: StateFlow<String> = _selectedModelId.asStateFlow()

    private val _selectedProviderType = MutableStateFlow(ProviderType.GEMINI)
    val selectedProviderType: StateFlow<ProviderType> = _selectedProviderType.asStateFlow()

    // Streaming state
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _currentStreamText = MutableStateFlow("")
    val currentStreamText: StateFlow<String> = _currentStreamText.asStateFlow()

    private val _currentThinkingText = MutableStateFlow("")
    val currentThinkingText: StateFlow<String> = _currentThinkingText.asStateFlow()

    // Text To Speech
    private var tts: TextToSpeech? = null
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    // Flow collections from Room
    val workspaces: StateFlow<List<WorkspaceEntity>> = repository.allWorkspaces
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val providers: StateFlow<List<AiProviderEntity>> = repository.allProviders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val skills: StateFlow<List<SkillEntity>> = repository.allSkills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val plugins: StateFlow<List<PluginEntity>> = repository.allPlugins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val agents: StateFlow<List<CustomAgentEntity>> = repository.allAgents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val repos: StateFlow<List<GithubRepoEntity>> = repository.allRepos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val docs: StateFlow<List<KnowledgeDocEntity>> = repository.allDocs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val generatedImages: StateFlow<List<GeneratedImageEntity>> = repository.allGeneratedImages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chats: StateFlow<List<ChatEntity>> = _selectedWorkspaceId
        .flatMapLatest { wsId -> repository.getChatsForWorkspace(wsId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentMessages: StateFlow<List<ChatMessageEntity>> = _selectedChatId
        .flatMapLatest { chatId -> repository.getMessagesForChat(chatId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                repository.initializeDefaultDataIfNeeded()
            } catch (e: Exception) {
                android.util.Log.e("WorkspaceViewModel", "Error initializing repository data", e)
            }
        }
        initTts()
    }

    private fun initTts() {
        try {
            tts = TextToSpeech(getApplication()) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    try {
                        tts?.language = Locale.US
                    } catch (e: Exception) {
                        android.util.Log.w("WorkspaceViewModel", "Failed to set TTS language", e)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.w("WorkspaceViewModel", "TTS service unavailable on this device", e)
        }
    }

    fun navigateTo(screen: NavigationScreen) {
        _currentScreen.value = screen
    }

    fun selectWorkspace(workspaceId: String) {
        _selectedWorkspaceId.value = workspaceId
    }

    fun selectChat(chatId: String) {
        _selectedChatId.value = chatId
    }

    fun selectModel(modelId: String, providerType: ProviderType) {
        _selectedModelId.value = modelId
        _selectedProviderType.value = providerType
        viewModelScope.launch {
            repository.updateChatModel(_selectedChatId.value, modelId, providerType)
        }
    }

    fun createNewChat(title: String = "New Chat Session") {
        viewModelScope.launch {
            val newChatId = repository.createChat(
                workspaceId = _selectedWorkspaceId.value,
                title = title,
                modelId = _selectedModelId.value,
                providerType = _selectedProviderType.value
            )
            _selectedChatId.value = newChatId
            _currentScreen.value = NavigationScreen.CHAT
        }
    }

    fun sendMessage(userText: String, imageBase64: String? = null) {
        if (userText.isBlank() || _isGenerating.value) return

        val chatId = _selectedChatId.value
        val userMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            sender = MessageSender.USER,
            content = userText,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.addMessage(userMsg)

            _isGenerating.value = true
            _currentStreamText.value = ""
            _currentThinkingText.value = "Analyzing context, loading workspace plugins & models..."

            // Get provider API Key
            val providerList = repository.allProviders.firstOrNull() ?: emptyList()
            val currentProvider = providerList.find { it.type == _selectedProviderType.value }
            var apiKey = currentProvider?.apiKey ?: ""

            if (apiKey.isBlank() && _selectedProviderType.value != ProviderType.GEMINI) {
                val noKeyMsg = ChatMessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = chatId,
                    sender = MessageSender.ASSISTANT,
                    content = "Configure an API provider to start chatting. You can set your API key in Settings or the Provider Manager.",
                    thinkingContent = "No API key found for provider ${_selectedProviderType.value.name}.",
                    modelUsed = _selectedModelId.value,
                    timestamp = System.currentTimeMillis()
                )
                repository.addMessage(noKeyMsg)
                _isGenerating.value = false
                _currentStreamText.value = ""
                _currentThinkingText.value = ""
                return@launch
            }

            val history = currentMessages.value.map { Pair(it.sender.name, it.content) } + Pair("USER", userText)

            // Construct system instruction based on workspace skills, plugins, & docs
            val activeDocs = docs.value.joinToString("\n\n") { "[Doc: ${it.title}]: ${it.content}" }
            val activeSkills = skills.value.joinToString("\n") { "Skill '${it.name}': ${it.instructions}" }
            val systemInstruction = """
            You are Claude AI Assistant Workspace, a world-class autonomous software developer, engineer, and multi-modal intelligence.
            
            Attached Skills:
            $activeSkills
            
            Knowledge Base Context (RAG):
            $activeDocs
            
            Respond with precise Markdown, structured code blocks, and clear step-by-step logic.
            """.trimIndent()

            var fullText = ""
            try {
                AiApiClient.generateChatStream(
                    providerType = _selectedProviderType.value,
                    apiKey = apiKey,
                    baseUrl = currentProvider?.baseUrl ?: "",
                    modelId = _selectedModelId.value,
                    systemInstruction = systemInstruction,
                    messages = history,
                    imageBase64 = imageBase64
                ).collect { chunk ->
                    fullText += chunk
                    _currentStreamText.value = fullText
                }
            } catch (e: Exception) {
                fullText = "API Connection Error: ${e.localizedMessage ?: "Network or provider unavailable."}\n\nConfigure an API provider in Settings to start chatting."
            }

            // Save assistant message to Room DB
            val assistantMsg = ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                sender = MessageSender.ASSISTANT,
                content = fullText,
                thinkingContent = "Execution finished using ${_selectedModelId.value} (${_selectedProviderType.value.name}).",
                modelUsed = _selectedModelId.value,
                timestamp = System.currentTimeMillis()
            )
            repository.addMessage(assistantMsg)

            _isGenerating.value = false
            _currentStreamText.value = ""
            _currentThinkingText.value = ""
        }
    }

    fun speakText(text: String) {
        if (_isSpeaking.value) {
            tts?.stop()
            _isSpeaking.value = false
        } else {
            tts?.speak(text.take(500), TextToSpeech.QUEUE_FLUSH, null, "UtteranceId")
            _isSpeaking.value = true
        }
    }

    fun togglePlugin(pluginId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.togglePlugin(pluginId, isEnabled)
        }
    }

    fun saveProviderKey(providerId: String, apiKey: String, baseUrl: String = "") {
        viewModelScope.launch {
            repository.saveProviderApiKey(providerId, apiKey, baseUrl)
        }
    }

    fun createWorkspace(name: String, description: String, iconName: String) {
        viewModelScope.launch {
            val id = repository.createWorkspace(name, description, iconName)
            _selectedWorkspaceId.value = id
        }
    }

    fun createSkill(name: String, description: String, instructions: String) {
        viewModelScope.launch {
            val skill = SkillEntity(
                id = "skill-" + UUID.randomUUID().toString().take(8),
                name = name,
                description = description,
                instructions = instructions
            )
            repository.saveSkill(skill)
        }
    }

    fun deleteSkill(skillId: String) {
        viewModelScope.launch {
            repository.deleteSkill(skillId)
        }
    }

    fun createAgent(name: String, description: String, systemPrompt: String, modelId: String, providerType: ProviderType) {
        viewModelScope.launch {
            val agent = CustomAgentEntity(
                id = "agent-" + UUID.randomUUID().toString().take(8),
                name = name,
                description = description,
                systemPrompt = systemPrompt,
                modelId = modelId,
                providerType = providerType
            )
            repository.saveAgent(agent)
        }
    }

    fun deleteAgent(agentId: String) {
        viewModelScope.launch {
            repository.deleteAgent(agentId)
        }
    }

    fun addKnowledgeDoc(title: String, content: String, fileType: String) {
        viewModelScope.launch {
            val doc = KnowledgeDocEntity(
                id = "doc-" + UUID.randomUUID().toString().take(8),
                workspaceId = _selectedWorkspaceId.value,
                title = title,
                content = content,
                fileType = fileType,
                wordCount = content.split("\\s+".toRegex()).size,
                chunkCount = (content.length / 500) + 1
            )
            repository.saveKnowledgeDoc(doc)
        }
    }

    fun deleteKnowledgeDoc(docId: String) {
        viewModelScope.launch {
            repository.deleteKnowledgeDoc(docId)
        }
    }

    fun addGithubRepo(name: String, owner: String, url: String) {
        viewModelScope.launch {
            val repo = GithubRepoEntity(
                id = "repo-" + UUID.randomUUID().toString().take(8),
                name = name,
                owner = owner,
                url = url
            )
            repository.saveRepo(repo)
        }
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            repository.deleteChat(chatId)
            val remaining = chats.value.filter { it.id != chatId }
            if (remaining.isNotEmpty()) {
                _selectedChatId.value = remaining.first().id
            } else {
                createNewChat("New Conversation")
            }
        }
    }

    // UI Panel & Theme Toggles
    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun cycleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.AMOLED
            AppThemeMode.AMOLED -> AppThemeMode.DARK
        }
    }

    fun toggleRightSidebar() {
        _isRightSidebarOpen.value = !_isRightSidebarOpen.value
    }

    fun setRightSidebarOpen(isOpen: Boolean) {
        _isRightSidebarOpen.value = isOpen
    }

    fun toggleCommandPalette() {
        _isCommandPaletteOpen.value = !_isCommandPaletteOpen.value
    }

    fun setCommandPaletteOpen(isOpen: Boolean) {
        _isCommandPaletteOpen.value = isOpen
    }

    fun toggleApiKeyModal() {
        _isApiKeyModalOpen.value = !_isApiKeyModalOpen.value
    }

    fun setApiKeyModalOpen(isOpen: Boolean) {
        _isApiKeyModalOpen.value = isOpen
    }

    fun toggleSettingsModal() {
        _isSettingsModalOpen.value = !_isSettingsModalOpen.value
    }

    fun setSettingsModalOpen(isOpen: Boolean) {
        _isSettingsModalOpen.value = isOpen
    }

    // Memory operations
    fun addMemory(title: String, content: String, type: MemoryType = MemoryType.LONG_TERM, category: String = "General") {
        viewModelScope.launch {
            val memory = MemoryEntity(
                id = "mem-" + UUID.randomUUID().toString().take(8),
                workspaceId = _selectedWorkspaceId.value,
                title = title,
                content = content,
                type = type,
                category = category
            )
            repository.saveMemory(memory)
        }
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    // Image Studio operations
    fun generateImage(prompt: String, style: String = "Futuristic AI", aspectRatio: String = "1:1") {
        viewModelScope.launch {
            // Unsplash placeholder URLs with distinct seeds for visual feedback
            val sampleImages = listOf(
                "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1634017839464-5c339ebe3cb4?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1614728894747-a83421e2b9c9?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1620641788421-7a1c342ea42e?w=800&auto=format&fit=crop&q=80"
            )
            val randomImg = sampleImages.random()
            val newImg = GeneratedImageEntity(
                id = "img-" + UUID.randomUUID().toString().take(8),
                prompt = prompt,
                aspectRatio = aspectRatio,
                imageUrl = randomImg,
                modelUsed = "Gemini 2.5 Flash Image",
                styleCategory = style
            )
            repository.saveGeneratedImage(newImg)
        }
    }

    fun deleteGeneratedImage(id: String) {
        viewModelScope.launch {
            repository.deleteGeneratedImage(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
