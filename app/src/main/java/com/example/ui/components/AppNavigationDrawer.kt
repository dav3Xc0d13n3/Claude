package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.NavigationScreen
import com.example.ui.viewmodel.WorkspaceViewModel

@Composable
fun AppNavigationDrawer(
    viewModel: WorkspaceViewModel,
    drawerState: DrawerState,
    onCloseDrawer: () -> Unit,
    content: @Composable () -> Unit
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val chats by viewModel.chats.collectAsState()
    val selectedChatId by viewModel.selectedChatId.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Drawer Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = ClaudeTerracotta
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("✦", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }
                        Column {
                            Text("Claude Workspace", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Multi-Provider AI Platform", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Primary Navigation Items
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        DrawerNavItem(
                            title = "AI Chat Workspace",
                            icon = Icons.Default.ChatBubbleOutline,
                            isSelected = currentScreen == NavigationScreen.CHAT,
                            onClick = {
                                viewModel.navigateTo(NavigationScreen.CHAT)
                                onCloseDrawer()
                            }
                        )
                        DrawerNavItem(
                            title = "Providers & Models",
                            icon = Icons.Default.VpnKey,
                            isSelected = currentScreen == NavigationScreen.PROVIDERS,
                            onClick = {
                                viewModel.navigateTo(NavigationScreen.PROVIDERS)
                                onCloseDrawer()
                            }
                        )
                        DrawerNavItem(
                            title = "Workspaces & Teams",
                            icon = Icons.Default.Folder,
                            isSelected = currentScreen == NavigationScreen.WORKSPACES,
                            onClick = {
                                viewModel.navigateTo(NavigationScreen.WORKSPACES)
                                onCloseDrawer()
                            }
                        )
                        DrawerNavItem(
                            title = "Plugin Marketplace",
                            icon = Icons.Default.Extension,
                            isSelected = currentScreen == NavigationScreen.PLUGINS,
                            onClick = {
                                viewModel.navigateTo(NavigationScreen.PLUGINS)
                                onCloseDrawer()
                            }
                        )
                        DrawerNavItem(
                            title = "Custom Skills",
                            icon = Icons.Default.Psychology,
                            isSelected = currentScreen == NavigationScreen.SKILLS,
                            onClick = {
                                viewModel.navigateTo(NavigationScreen.SKILLS)
                                onCloseDrawer()
                            }
                        )
                        DrawerNavItem(
                            title = "Autonomous Agents",
                            icon = Icons.Default.SmartToy,
                            isSelected = currentScreen == NavigationScreen.AGENTS,
                            onClick = {
                                viewModel.navigateTo(NavigationScreen.AGENTS)
                                onCloseDrawer()
                            }
                        )
                        DrawerNavItem(
                            title = "GitHub Integration",
                            icon = Icons.Default.Code,
                            isSelected = currentScreen == NavigationScreen.GITHUB,
                            onClick = {
                                viewModel.navigateTo(NavigationScreen.GITHUB)
                                onCloseDrawer()
                            }
                        )
                        DrawerNavItem(
                            title = "Knowledge Base (RAG)",
                            icon = Icons.Default.Description,
                            isSelected = currentScreen == NavigationScreen.KNOWLEDGE_BASE,
                            onClick = {
                                viewModel.navigateTo(NavigationScreen.KNOWLEDGE_BASE)
                                onCloseDrawer()
                            }
                        )
                        DrawerNavItem(
                            title = "Memory Center",
                            icon = Icons.Default.Bookmark,
                            isSelected = currentScreen == NavigationScreen.MEMORY,
                            onClick = {
                                viewModel.navigateTo(NavigationScreen.MEMORY)
                                onCloseDrawer()
                            }
                        )
                        DrawerNavItem(
                            title = "Image Generation Studio",
                            icon = Icons.Default.AutoAwesome,
                            isSelected = currentScreen == NavigationScreen.IMAGE_STUDIO,
                            onClick = {
                                viewModel.navigateTo(NavigationScreen.IMAGE_STUDIO)
                                onCloseDrawer()
                            }
                        )
                        DrawerNavItem(
                            title = "Voice Interface",
                            icon = Icons.Default.Mic,
                            isSelected = currentScreen == NavigationScreen.VOICE_STUDIO,
                            onClick = {
                                viewModel.navigateTo(NavigationScreen.VOICE_STUDIO)
                                onCloseDrawer()
                            }
                        )
                        DrawerNavItem(
                            title = "Settings",
                            icon = Icons.Default.Settings,
                            isSelected = currentScreen == NavigationScreen.SETTINGS,
                            onClick = {
                                viewModel.navigateTo(NavigationScreen.SETTINGS)
                                onCloseDrawer()
                            }
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Recent Chats List
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Conversations",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(onClick = {
                            viewModel.createNewChat()
                            onCloseDrawer()
                        }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "New", tint = ClaudeTerracotta)
                        }
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(chats) { chat ->
                            val isSelected = chat.id == selectedChatId
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.selectChat(chat.id)
                                        viewModel.navigateTo(NavigationScreen.CHAT)
                                        onCloseDrawer()
                                    },
                                color = if (isSelected) ClaudeTerracotta.copy(alpha = 0.15f) else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (chat.isPinned) Icons.Default.PushPin else Icons.Default.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = if (isSelected) ClaudeTerracotta else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = chat.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) ClaudeTerracotta else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) {
        content()
    }
}

@Composable
fun DrawerNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        color = if (isSelected) ClaudeTerracotta.copy(alpha = 0.15f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) ClaudeTerracotta else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) ClaudeTerracotta else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
