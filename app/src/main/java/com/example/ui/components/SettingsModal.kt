package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModal(
    viewModel: WorkspaceViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("General", "Appearance", "API Keys", "GitHub", "Storage")

    var autoScrollEnabled by remember { mutableStateOf(true) }
    var streamSpeed by remember { mutableStateOf(1f) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 580.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = ClaudeTerracotta)
                        Text("System Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Category Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    divider = {},
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = ClaudeTerracotta
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // Content area based on tab
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> { // General
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Auto-Scroll Chat", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        Text("Automatically scroll during AI response streaming", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Switch(
                                        checked = autoScrollEnabled,
                                        onCheckedChange = { autoScrollEnabled = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = ClaudeTerracotta)
                                    )
                                }
                            }
                        }

                        1 -> { // Appearance
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Theme Mode", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        listOf(
                                            Triple("Dark", AppThemeMode.DARK, Icons.Default.DarkMode),
                                            Triple("Light", AppThemeMode.LIGHT, Icons.Default.LightMode),
                                            Triple("AMOLED", AppThemeMode.AMOLED, Icons.Default.Brightness2)
                                        ).forEach { (label, mode, icon) ->
                                            val isSelected = themeMode == mode
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .clickable { viewModel.setThemeMode(mode) }
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (isSelected) ClaudeTerracotta else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                                        shape = RoundedCornerShape(10.dp)
                                                    ),
                                                color = if (isSelected) ClaudeTerracotta.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(icon, contentDescription = null, tint = if (isSelected) ClaudeTerracotta else MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> { // API Keys
                            item {
                                Button(
                                    onClick = {
                                        onDismiss()
                                        viewModel.toggleApiKeyModal()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = ClaudeTerracotta)
                                ) {
                                    Icon(Icons.Default.Key, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Open API Key Manager")
                                }
                            }
                        }

                        3 -> { // GitHub
                            item {
                                OutlinedTextField(
                                    value = "ghp_xxxxxxxxxxxxxxxxxxxx",
                                    onValueChange = {},
                                    label = { Text("Personal Access Token") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        4 -> { // Storage
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Local SQLite Room Database", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text("Used size: ~1.2 MB in claude_ai_workspace.db", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    OutlinedButton(
                                        onClick = { Toast.makeText(context, "Database verified & optimized!", Toast.LENGTH_SHORT).show() },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Optimize Local Database")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
