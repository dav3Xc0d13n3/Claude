package com.example.aiworkspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.ClaudeAiWorkspaceTheme
import com.example.ui.viewmodel.NavigationScreen
import com.example.ui.viewmodel.WorkspaceViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: WorkspaceViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val isRightSidebarOpen by viewModel.isRightSidebarOpen.collectAsState()
            val isCommandPaletteOpen by viewModel.isCommandPaletteOpen.collectAsState()
            val isApiKeyModalOpen by viewModel.isApiKeyModalOpen.collectAsState()
            val isSettingsModalOpen by viewModel.isSettingsModalOpen.collectAsState()

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            var showModelSelectorModal by remember { mutableStateOf(false) }

            ClaudeAiWorkspaceTheme(themeMode = themeMode) {
                // Back Button & Gesture Handler
                androidx.activity.compose.BackHandler(
                    enabled = drawerState.isOpen || isRightSidebarOpen || isCommandPaletteOpen || isApiKeyModalOpen || isSettingsModalOpen || showModelSelectorModal || currentScreen != NavigationScreen.CHAT
                ) {
                    when {
                        drawerState.isOpen -> scope.launch { drawerState.close() }
                        isRightSidebarOpen -> viewModel.setRightSidebarOpen(false)
                        isCommandPaletteOpen -> viewModel.setCommandPaletteOpen(false)
                        isApiKeyModalOpen -> viewModel.setApiKeyModalOpen(false)
                        isSettingsModalOpen -> viewModel.setSettingsModalOpen(false)
                        showModelSelectorModal -> showModelSelectorModal = false
                        currentScreen != NavigationScreen.CHAT -> viewModel.navigateTo(NavigationScreen.CHAT)
                    }
                }

                // Global Modals
                if (showModelSelectorModal) {
                    val selectedModelId by viewModel.selectedModelId.collectAsState()
                    ModelSelectorDialog(
                        currentModelId = selectedModelId,
                        onModelSelected = { modelId, provider ->
                            viewModel.selectModel(modelId, provider)
                        },
                        onDismiss = { showModelSelectorModal = false }
                    )
                }

                if (isCommandPaletteOpen) {
                    CommandPaletteDialog(
                        viewModel = viewModel,
                        onDismiss = { viewModel.setCommandPaletteOpen(false) }
                    )
                }

                if (isApiKeyModalOpen) {
                    ApiKeyManagerModal(
                        viewModel = viewModel,
                        onDismiss = { viewModel.setApiKeyModalOpen(false) }
                    )
                }

                if (isSettingsModalOpen) {
                    SettingsModal(
                        viewModel = viewModel,
                        onDismiss = { viewModel.setSettingsModalOpen(false) }
                    )
                }

                AppNavigationDrawer(
                    viewModel = viewModel,
                    drawerState = drawerState,
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    }
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Top Navigation Header Bar
                        TopNavigationBar(
                            viewModel = viewModel,
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onOpenModelSelector = { showModelSelectorModal = true }
                        )

                        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            // Main Workspace Screen
                            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                when (currentScreen) {
                                    NavigationScreen.CHAT -> ChatScreen(
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                    NavigationScreen.PROVIDERS -> ProvidersScreen(
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                    NavigationScreen.WORKSPACES -> WorkspacesScreen(
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                    NavigationScreen.PLUGINS -> PluginsScreen(
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                    NavigationScreen.SKILLS -> SkillsScreen(
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                    NavigationScreen.AGENTS -> AgentsScreen(
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                    NavigationScreen.GITHUB -> GithubScreen(
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                    NavigationScreen.KNOWLEDGE_BASE -> KnowledgeBaseScreen(
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                    NavigationScreen.PLAYGROUND -> PlaygroundScreen(
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                    NavigationScreen.MEMORY -> MemoryScreen(
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                    NavigationScreen.IMAGE_STUDIO -> ImageStudioScreen(
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                    NavigationScreen.VOICE_STUDIO -> VoiceStudioScreen(
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                    NavigationScreen.SETTINGS -> SettingsScreen(
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                }
                            }

                            // Collapsible Right Context Panel
                            RightSidebarContextPanel(
                                viewModel = viewModel,
                                isOpen = isRightSidebarOpen,
                                onClose = { viewModel.setRightSidebarOpen(false) }
                            )
                        }
                    }
                }
            }
        }
    }
}
