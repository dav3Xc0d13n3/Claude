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
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeBaseScreen(
    viewModel: WorkspaceViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val docs by viewModel.docs.collectAsState()

    var showAddDocDialog by remember { mutableStateOf(false) }
    var docTitle by remember { mutableStateOf("") }
    var docContent by remember { mutableStateOf("") }

    if (showAddDocDialog) {
        AlertDialog(
            onDismissRequest = { showAddDocDialog = false },
            title = { Text("Upload Knowledge Document") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = docTitle,
                        onValueChange = { docTitle = it },
                        label = { Text("Document Title (e.g., API_Spec.pdf)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = docContent,
                        onValueChange = { docContent = it },
                        label = { Text("Raw Document Text / Code Snippets") },
                        minLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (docTitle.isNotBlank() && docContent.isNotBlank()) {
                            viewModel.addKnowledgeDoc(docTitle, docContent, "PDF")
                            showAddDocDialog = false
                            docTitle = ""
                            docContent = ""
                            Toast.makeText(context, "Knowledge document indexed & chunked for RAG", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClaudeTerracotta)
                ) {
                    Text("Index Document")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDocDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Knowledge Base & RAG Index", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDocDialog = true }) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Upload Doc", tint = ClaudeTerracotta)
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
                text = "Indexed Vector Embeddings & Docs (${docs.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(docs) { doc ->
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
                                    Icon(Icons.Default.Description, contentDescription = null, tint = ClaudeTerracotta)
                                    Text(
                                        text = doc.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                IconButton(onClick = { viewModel.deleteKnowledgeDoc(doc.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Words: ${doc.wordCount}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Chunks: ${doc.chunkCount}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ClaudeTerracotta
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = doc.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(8.dp),
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
