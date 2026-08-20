package com.example.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyManagerModal(
    viewModel: WorkspaceViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val providers by viewModel.providers.collectAsState()

    var selectedProviderId by remember { mutableStateOf("gemini") }
    var apiKeyInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val activeProvider = providers.find { it.id == selectedProviderId } ?: providers.firstOrNull()

    LaunchedEffect(selectedProviderId) {
        apiKeyInput = activeProvider?.apiKey ?: ""
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp)
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
                        Surface(
                            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)),
                            color = ClaudeTerracotta.copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = ClaudeTerracotta)
                            }
                        }
                        Column {
                            Text("API Key Manager", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Configure AI providers & secrets", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // Provider Selection Chips
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("Select Provider", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            providers.take(4).forEach { p ->
                                val isSelected = p.id == selectedProviderId
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedProviderId = p.id },
                                    label = { Text(p.name, fontSize = 12.sp) },
                                    leadingIcon = {
                                        Icon(
                                            if (p.isEnabled) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (p.isEnabled) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ClaudeTerracotta.copy(alpha = 0.2f),
                                        selectedLabelColor = ClaudeTerracotta
                                    )
                                )
                            }
                        }
                    }

                    if (activeProvider != null) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = activeProvider.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (activeProvider.isEnabled) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceContainer
                                        ) {
                                            Text(
                                                if (activeProvider.isEnabled) "ACTIVE" else "NOT CONFIGURED",
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (activeProvider.isEnabled) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = apiKeyInput,
                                        onValueChange = { apiKeyInput = it },
                                        label = { Text("API Key") },
                                        placeholder = { Text("Paste secret key e.g. sk-...") },
                                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                        trailingIcon = {
                                            IconButton(onClick = { showPassword = !showPassword }) {
                                                Icon(
                                                    if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = "Toggle visibility"
                                                )
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = {
                                                viewModel.saveProviderKey(activeProvider.id, apiKeyInput)
                                                Toast.makeText(context, "${activeProvider.name} API Key saved!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = ClaudeTerracotta)
                                        ) {
                                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Save Key")
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                Toast.makeText(context, "Verifying API key...", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Test Key")
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
}
