package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    viewModel: WorkspaceViewModel,
    onOpenDrawer: () -> Unit,
    onOpenModelSelector: () -> Unit
) {
    val selectedModelId by viewModel.selectedModelId.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isRightSidebarOpen by viewModel.isRightSidebarOpen.collectAsState()
    val workspaces by viewModel.workspaces.collectAsState()
    val selectedWorkspaceId by viewModel.selectedWorkspaceId.collectAsState()

    val currentWorkspace = workspaces.find { it.id == selectedWorkspaceId }
    var showWorkspaceDropdown by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
            ),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Drawer Button + Logo + Workspace Selector Dropdown
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onOpenDrawer, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface)
                }

                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = ClaudeTerracotta
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("✦", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                // Workspace Dropdown Selector
                Box {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showWorkspaceDropdown = true }
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = currentWorkspace?.name ?: "Main Workspace",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showWorkspaceDropdown,
                        onDismissRequest = { showWorkspaceDropdown = false }
                    ) {
                        workspaces.forEach { ws ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(ws.name, fontWeight = FontWeight.SemiBold)
                                        Text(ws.description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                onClick = {
                                    viewModel.selectWorkspace(ws.id)
                                    showWorkspaceDropdown = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Folder, contentDescription = null, tint = ClaudeTerracotta)
                                }
                            )
                        }
                    }
                }

                // Active Model Picker Pill
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenModelSelector() }
                        .border(1.dp, ClaudeTerracotta.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    color = ClaudeTerracotta.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🤖", fontSize = 12.sp)
                        Text(
                            text = selectedModelId,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ClaudeTerracotta
                        )
                        Icon(
                            Icons.Default.SwapHoriz,
                            contentDescription = "Switch Model",
                            modifier = Modifier.size(14.dp),
                            tint = ClaudeTerracotta
                        )
                    }
                }
            }

            // Center: Command Palette Search Bar Trigger (Ctrl+K)
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { viewModel.toggleCommandPalette() }
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Search or Ctrl+K...",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        Text(
                            text = "⌘K",
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Right Actions: Theme Switcher, Keys Modal, Settings, Right Context Panel Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Theme Cycle Button (Dark / Light / AMOLED)
                IconButton(
                    onClick = { viewModel.cycleThemeMode() },
                    modifier = Modifier.size(34.dp)
                ) {
                    val icon = when (themeMode) {
                        AppThemeMode.DARK -> Icons.Default.DarkMode
                        AppThemeMode.LIGHT -> Icons.Default.LightMode
                        AppThemeMode.AMOLED -> Icons.Default.Brightness2
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Theme Mode",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // API Key Manager Modal Trigger
                IconButton(
                    onClick = { viewModel.toggleApiKeyModal() },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "API Keys",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Settings Dialog Trigger
                IconButton(
                    onClick = { viewModel.toggleSettingsModal() },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Right Context Panel Toggle
                IconButton(
                    onClick = { viewModel.toggleRightSidebar() },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (isRightSidebarOpen) Icons.Default.ViewSidebar else Icons.Default.VerticalSplit,
                        contentDescription = "Context Panel",
                        tint = if (isRightSidebarOpen) ClaudeTerracotta else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
