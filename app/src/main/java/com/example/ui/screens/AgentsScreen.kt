package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CustomAgentEntity
import com.example.data.model.ProviderType
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.NavigationScreen
import com.example.ui.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentsScreen(
    viewModel: WorkspaceViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val agents by viewModel.agents.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var agentName by remember { mutableStateOf("") }
    var agentDesc by remember { mutableStateOf("") }
    var agentPrompt by remember { mutableStateOf("") }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Build Custom AI Agent") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = agentName,
                        onValueChange = { agentName = it },
                        label = { Text("Agent Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = agentDesc,
                        onValueChange = { agentDesc = it },
                        label = { Text("Role Description") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = agentPrompt,
                        onValueChange = { agentPrompt = it },
                        label = { Text("System Persona & Execution Workflow") },
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (agentName.isNotBlank() && agentPrompt.isNotBlank()) {
                            viewModel.createAgent(
                                name = agentName,
                                description = agentDesc,
                                systemPrompt = agentPrompt,
                                modelId = "gemini-3.1-pro-preview",
                                providerType = ProviderType.GEMINI
                            )
                            showCreateDialog = false
                            agentName = ""
                            agentDesc = ""
                            agentPrompt = ""
                            Toast.makeText(context, "Agent deployed", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClaudeTerracotta)
                ) {
                    Text("Deploy Agent")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Autonomous AI Agents", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Agent", tint = ClaudeTerracotta)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Deployed Workflows & Copilots (${agents.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(agents) { agent ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = ClaudeTerracotta)
                                    Text(
                                        text = agent.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                IconButton(onClick = { viewModel.deleteAgent(agent.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }

                            Text(
                                text = agent.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Model: ${agent.modelId} (${agent.providerType.name})",
                                style = MaterialTheme.typography.labelSmall,
                                color = ClaudeTerracotta
                            )

                            Button(
                                onClick = {
                                    viewModel.createNewChat("Agent Task: ${agent.name}")
                                    viewModel.sendMessage("Execute task using agent persona '${agent.name}': ${agent.systemPrompt}")
                                    viewModel.navigateTo(NavigationScreen.CHAT)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Execute Agent Workflow", color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                }
            }
        }
    }
}
