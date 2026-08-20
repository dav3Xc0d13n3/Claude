package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MemoryEntity
import com.example.data.model.MemoryType
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(
    viewModel: WorkspaceViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val memories by viewModel.memories.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MemoryType.LONG_TERM) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = ClaudeTerracotta)
                        Text("Memory Center", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ClaudeTerracotta),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add Fact", fontSize = 12.sp)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overview banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, ClaudeTerracotta.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                color = ClaudeTerracotta.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = ClaudeTerracotta, modifier = Modifier.size(32.dp))
                    Column {
                        Text("Long-Term AI Memory Engine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Information stored here is injected into prompt contexts across conversations.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Text("Stored Context & Preferences (${memories.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            if (memories.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No stored memories yet. Tap 'Add Fact' above to create one.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(memories) { mem ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(mem.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.surfaceContainer
                                        ) {
                                            Text(
                                                mem.category,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(mem.content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                IconButton(onClick = { viewModel.deleteMemory(mem.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Memory Fact") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Fact Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = contentInput,
                        onValueChange = { contentInput = it },
                        label = { Text("Memory Content / Instruction") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleInput.isNotBlank()) {
                            viewModel.addMemory(titleInput, contentInput, selectedType)
                            showAddDialog = false
                            titleInput = ""
                            contentInput = ""
                            Toast.makeText(context, "Memory fact stored!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClaudeTerracotta)
                ) {
                    Text("Save Fact")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
