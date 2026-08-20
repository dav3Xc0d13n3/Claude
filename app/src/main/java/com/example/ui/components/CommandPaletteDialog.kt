package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.NavigationScreen
import com.example.ui.viewmodel.WorkspaceViewModel

data class CommandItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val category: String,
    val onSelect: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandPaletteDialog(
    viewModel: WorkspaceViewModel,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val chats by viewModel.chats.collectAsState()

    // Build items list
    val allCommands = remember(chats) {
        mutableListOf<CommandItem>().apply {
            // Screen Navigation Commands
            add(CommandItem("nav-chat", "Go to AI Chat Workspace", "Primary conversational interface", Icons.Default.ChatBubble, "Navigation") {
                viewModel.navigateTo(NavigationScreen.CHAT)
                onDismiss()
            })
            add(CommandItem("nav-providers", "Go to Providers & API Keys", "Manage API keys and discovery", Icons.Default.Key, "Navigation") {
                viewModel.navigateTo(NavigationScreen.PROVIDERS)
                onDismiss()
            })
            add(CommandItem("nav-github", "Go to GitHub Workspace", "Repositories, PRs, and commits", Icons.Default.Code, "Navigation") {
                viewModel.navigateTo(NavigationScreen.GITHUB)
                onDismiss()
            })
            add(CommandItem("nav-plugins", "Go to Plugin Marketplace", "Extensions & integrations", Icons.Default.Extension, "Navigation") {
                viewModel.navigateTo(NavigationScreen.PLUGINS)
                onDismiss()
            })
            add(CommandItem("nav-skills", "Go to Custom Skills", "Prompt engineering & auditors", Icons.Default.Psychology, "Navigation") {
                viewModel.navigateTo(NavigationScreen.SKILLS)
                onDismiss()
            })
            add(CommandItem("nav-agents", "Go to Autonomous Agents", "Copilots & agent builders", Icons.Default.SmartToy, "Navigation") {
                viewModel.navigateTo(NavigationScreen.AGENTS)
                onDismiss()
            })
            add(CommandItem("nav-knowledge", "Go to Knowledge Base (RAG)", "Upload and index documents", Icons.Default.Description, "Navigation") {
                viewModel.navigateTo(NavigationScreen.KNOWLEDGE_BASE)
                onDismiss()
            })
            add(CommandItem("nav-memory", "Go to Memory Center", "Long-term facts and notes", Icons.Default.Bookmark, "Navigation") {
                viewModel.navigateTo(NavigationScreen.MEMORY)
                onDismiss()
            })
            add(CommandItem("nav-image", "Go to Image Studio", "Generative Midjourney-style art", Icons.Default.AutoAwesome, "Navigation") {
                viewModel.navigateTo(NavigationScreen.IMAGE_STUDIO)
                onDismiss()
            })
            add(CommandItem("nav-voice", "Go to Voice Interface", "Speech transcription & audio TTS", Icons.Default.Mic, "Navigation") {
                viewModel.navigateTo(NavigationScreen.VOICE_STUDIO)
                onDismiss()
            })
            add(CommandItem("nav-settings", "Open System Settings", "Categorized preferences", Icons.Default.Settings, "Navigation") {
                viewModel.navigateTo(NavigationScreen.SETTINGS)
                onDismiss()
            })

            // Actions Commands
            add(CommandItem("act-new-chat", "Create New Conversation", "Start a fresh chat thread", Icons.Default.Add, "Actions") {
                viewModel.createNewChat("New Conversation")
                viewModel.navigateTo(NavigationScreen.CHAT)
                onDismiss()
            })
            add(CommandItem("act-theme-dark", "Switch to Dark Theme", "Claude minimal dark aesthetic", Icons.Default.DarkMode, "Appearance") {
                viewModel.setThemeMode(AppThemeMode.DARK)
                onDismiss()
            })
            add(CommandItem("act-theme-light", "Switch to Light Theme", "Clean bright warm aesthetic", Icons.Default.LightMode, "Appearance") {
                viewModel.setThemeMode(AppThemeMode.LIGHT)
                onDismiss()
            })
            add(CommandItem("act-theme-amoled", "Switch to AMOLED Pure Black", "OLED pitch black aesthetic", Icons.Default.Brightness2, "Appearance") {
                viewModel.setThemeMode(AppThemeMode.AMOLED)
                onDismiss()
            })

            // Recent Chats
            chats.forEach { chat ->
                add(CommandItem("chat-${chat.id}", chat.title, "Chat thread (${chat.modelId})", Icons.Default.Chat, "Recent Chats") {
                    viewModel.selectChat(chat.id)
                    viewModel.navigateTo(NavigationScreen.CHAT)
                    onDismiss()
                })
            }
        }
    }

    val filteredCommands = remember(searchQuery, allCommands) {
        if (searchQuery.isBlank()) {
            allCommands
        } else {
            allCommands.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.subtitle.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 520.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, ClaudeTerracotta.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Type a command, search chats, or switch screens...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = ClaudeTerracotta)
                    },
                    trailingIcon = {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            Text("ESC", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ClaudeTerracotta,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    singleLine = true
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // Results list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredCommands) { cmd ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { cmd.onSelect() },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)),
                                    color = ClaudeTerracotta.copy(alpha = 0.15f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(cmd.icon, contentDescription = null, tint = ClaudeTerracotta, modifier = Modifier.size(18.dp))
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(cmd.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text(cmd.subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainer
                                ) {
                                    Text(
                                        cmd.category,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
