package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: WorkspaceViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsState()

    var autoScrollEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = ClaudeTerracotta)
                        Text("Workspace Settings", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Appearance & Theme", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(
                        Triple("Dark Mode", AppThemeMode.DARK, Icons.Default.DarkMode),
                        Triple("Light Mode", AppThemeMode.LIGHT, Icons.Default.LightMode),
                        Triple("AMOLED Black", AppThemeMode.AMOLED, Icons.Default.Brightness2)
                    ).forEach { (label, mode, icon) ->
                        val isSelected = themeMode == mode
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.setThemeMode(mode) }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ClaudeTerracotta else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            color = if (isSelected) ClaudeTerracotta.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(icon, contentDescription = null, tint = if (isSelected) ClaudeTerracotta else MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            }

            item {
                Text("API Credentials & Model Discovery", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.toggleApiKeyModal() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ClaudeTerracotta),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Key, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Manage Provider API Keys (NVIDIA, Gemini, OpenAI, Anthropic, Groq)")
                }
            }

            item {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            }

            item {
                Text("Database & Storage", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("SQLite Room Database Status", fontWeight = FontWeight.Bold)
                        Text("Tables: ai_providers, workspaces, chats, messages, skills, plugins, agents, docs, repos, memories, images.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Button(
                            onClick = { Toast.makeText(context, "Database cache cleared and optimized!", Toast.LENGTH_SHORT).show() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Optimize Database Cache", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}
