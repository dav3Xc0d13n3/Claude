package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.AiApiClient
import com.example.data.model.AiProviderEntity
import com.example.data.model.ProviderType
import com.example.ui.theme.*
import com.example.ui.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersScreen(
    viewModel: WorkspaceViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val providers by viewModel.providers.collectAsState()
    var selectedProviderType by remember { mutableStateOf(ProviderType.NVIDIA_NIM) }

    val activeProvider = providers.find { it.type == selectedProviderType }
    var apiKeyInput by remember(selectedProviderType) { mutableStateOf(activeProvider?.apiKey ?: "") }
    var baseUrlInput by remember(selectedProviderType) { mutableStateOf(activeProvider?.baseUrl ?: "") }

    val discoveredModels = remember(selectedProviderType) {
        AiApiClient.discoverModelsForProvider(selectedProviderType, apiKeyInput, baseUrlInput)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Providers & Model Discovery", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Provider Tabs Row
            ScrollableTabRow(
                selectedTabIndex = ProviderType.entries.indexOf(selectedProviderType),
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[ProviderType.entries.indexOf(selectedProviderType)]),
                        color = ClaudeTerracotta
                    )
                }
            ) {
                ProviderType.entries.forEach { p ->
                    Tab(
                        selected = p == selectedProviderType,
                        onClick = { selectedProviderType = p },
                        text = {
                            Text(
                                text = p.name.replace("_", " "),
                                fontWeight = if (p == selectedProviderType) FontWeight.Bold else FontWeight.Normal,
                                color = if (p == selectedProviderType) ClaudeTerracotta else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // Key Manager Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = ClaudeTerracotta)
                        Text(
                            text = "Configure ${selectedProviderType.name.replace("_", " ")} Key",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("API Key") },
                        placeholder = { Text("Enter ${selectedProviderType.name} secret key...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (selectedProviderType == ProviderType.CUSTOM) {
                        OutlinedTextField(
                            value = baseUrlInput,
                            onValueChange = { baseUrlInput = it },
                            label = { Text("Endpoint URL") },
                            placeholder = { Text("http://localhost:8080/v1/chat/completions") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Button(
                        onClick = {
                            val providerId = selectedProviderType.name.lowercase()
                            viewModel.saveProviderKey(providerId, apiKeyInput, baseUrlInput)
                            Toast.makeText(context, "Saved ${selectedProviderType.name} credentials", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = ClaudeTerracotta),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save & Validate")
                    }
                }
            }

            // Model Discovery Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Discovered Models (${discoveredModels.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Auto-detected",
                    style = MaterialTheme.typography.labelSmall,
                    color = ClaudeTerracotta
                )
            }

            // Discovered Models List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(discoveredModels) { model ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = model.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = model.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Context: ${model.contextWindow}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = model.pricingInfo,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ClaudeAccentGold
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.selectModel(model.id, model.providerType)
                                    viewModel.navigateTo(com.example.ui.viewmodel.NavigationScreen.CHAT)
                                    Toast.makeText(context, "Selected ${model.name}", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Use Model", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
