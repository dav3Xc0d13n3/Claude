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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.NavigationScreen
import com.example.ui.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspacesScreen(
    viewModel: WorkspaceViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val workspaces by viewModel.workspaces.collectAsState()
    val selectedWsId by viewModel.selectedWorkspaceId.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newWsName by remember { mutableStateOf("") }
    var newWsDesc by remember { mutableStateOf("") }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Workspace") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newWsName,
                        onValueChange = { newWsName = it },
                        label = { Text("Workspace Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newWsDesc,
                        onValueChange = { newWsDesc = it },
                        label = { Text("Description") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newWsName.isNotBlank()) {
                            viewModel.createWorkspace(newWsName, newWsDesc, "Terminal")
                            showCreateDialog = false
                            newWsName = ""
                            newWsDesc = ""
                            Toast.makeText(context, "Workspace created", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClaudeTerracotta)
                ) {
                    Text("Create")
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
                title = { Text("Workspaces & Teams", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Workspace", tint = ClaudeTerracotta)
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
                text = "Multi-Project Spaces (${workspaces.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(workspaces) { ws ->
                    val isSelected = ws.id == selectedWsId
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) ClaudeTerracotta else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                viewModel.selectWorkspace(ws.id)
                                viewModel.navigateTo(NavigationScreen.CHAT)
                            },
                        color = if (isSelected) ClaudeTerracotta.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = if (isSelected) ClaudeTerracotta else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ws.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = ws.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = ClaudeTerracotta)
                            }
                        }
                    }
                }
            }
        }
    }
}
