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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.AiApiClient
import com.example.data.model.ModelInfo
import com.example.data.model.ProviderType
import com.example.ui.theme.*

@Composable
fun ModelSelectorDialog(
    currentModelId: String,
    onModelSelected: (String, ProviderType) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedProviderFilter by remember { mutableStateOf<ProviderType?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val allModels = remember {
        ProviderType.entries.flatMap { provider ->
            AiApiClient.discoverModelsForProvider(provider, "")
        }
    }

    val filteredModels = allModels.filter { model ->
        (selectedProviderFilter == null || model.providerType == selectedProviderFilter) &&
        (searchQuery.isBlank() || model.name.contains(searchQuery, ignoreCase = true) || model.id.contains(searchQuery, ignoreCase = true))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = ClaudeTerracotta
                )
                Text(
                    text = "Select AI Model",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search models, context, capabilities...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Provider Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedProviderFilter == null,
                        onClick = { selectedProviderFilter = null },
                        label = { Text("All") }
                    )
                    ProviderType.entries.take(4).forEach { provider ->
                        FilterChip(
                            selected = selectedProviderFilter == provider,
                            onClick = { selectedProviderFilter = provider },
                            label = { Text(provider.name.take(6)) }
                        )
                    }
                }

                // Model Items
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredModels) { model ->
                        ModelCardItem(
                            model = model,
                            isSelected = model.id == currentModelId,
                            onClick = {
                                onModelSelected(model.id, model.providerType)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun ModelCardItem(
    model: ModelInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val providerColor = when (model.providerType) {
        ProviderType.NVIDIA_NIM -> ProviderNvidia
        ProviderType.GEMINI -> ProviderGemini
        ProviderType.OPENAI -> ProviderOpenAI
        ProviderType.ANTHROPIC -> ProviderAnthropic
        ProviderType.GROQ -> ProviderGroq
        ProviderType.OPENROUTER -> ProviderOpenRouter
        ProviderType.CUSTOM -> Color.Gray
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ClaudeTerracotta else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        color = if (isSelected) ClaudeTerracotta.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Provider Badge
                Box(
                    modifier = Modifier
                        .background(providerColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = model.providerName,
                        style = MaterialTheme.typography.labelSmall,
                        color = providerColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = model.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Capability Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Context: ${model.contextWindow}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )

                if (model.supportsReasoning) {
                    Text(
                        text = "🧠 Reasoning",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClaudeAccentGold,
                        fontSize = 11.sp
                    )
                }
                if (model.supportsVision) {
                    Text(
                        text = "👁️ Vision",
                        style = MaterialTheme.typography.labelSmall,
                        color = ProviderGemini,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
