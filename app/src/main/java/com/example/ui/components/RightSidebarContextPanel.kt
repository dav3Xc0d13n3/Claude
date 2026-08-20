package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.WorkspaceViewModel

@Composable
fun RightSidebarContextPanel(
    viewModel: WorkspaceViewModel,
    isOpen: Boolean,
    onClose: () -> Unit
) {
    val selectedModelId by viewModel.selectedModelId.collectAsState()
    val skills by viewModel.skills.collectAsState()
    val plugins by viewModel.plugins.collectAsState()
    val memories by viewModel.memories.collectAsState()
    val agents by viewModel.agents.collectAsState()
    val docs by viewModel.docs.collectAsState()

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        Surface(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                ),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Panel Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = null,
                            tint = ClaudeTerracotta,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Context & Memory",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // 1. Model & Token Gauge Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Active Model", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ClaudeTerracotta.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "ONLINE",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = ClaudeTerracotta,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(selectedModelId, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)

                        // Context Window Usage Gauge
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Token Context", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("3,420 / 200,000", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            LinearProgressIndicator(
                                progress = { 0.017f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ClaudeTerracotta,
                                trackColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        }
                    }
                }

                // 2. Active Skills Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Active Skills (${skills.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (skills.isEmpty()) {
                        Text("No skills attached", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        skills.take(3).forEach { skill ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp)),
                                color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = ClaudeTerracotta, modifier = Modifier.size(16.dp))
                                    Text(skill.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, maxLines = 1)
                                }
                            }
                        }
                    }
                }

                // 3. Active Plugins Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val enabledPlugins = plugins.filter { it.isEnabled }
                    Text(
                        "Enabled Tools & Plugins (${enabledPlugins.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    enabledPlugins.take(4).forEach { plugin ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Extension, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Text(plugin.name, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                            }
                        }
                    }
                }

                // 4. Stored Memory Facts
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Memory Bank (${memories.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Edit",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClaudeTerracotta,
                            modifier = Modifier.clickable { viewModel.navigateTo(com.example.ui.viewmodel.NavigationScreen.MEMORY) }
                        )
                    }

                    memories.take(3).forEach { mem ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(mem.title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    mem.content,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }

                // 5. Knowledge Documents / RAG Status
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "RAG Knowledge Base (${docs.size} files)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    docs.take(2).forEach { doc ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Text(doc.title, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        }
                    }
                }

                // 6. Agent Copilot Status
                val activeAgent = agents.firstOrNull()
                if (activeAgent != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)),
                        color = ClaudeTerracotta.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, tint = ClaudeTerracotta)
                            Column {
                                Text(activeAgent.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = ClaudeTerracotta)
                                Text("Copilot Agent Active", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
