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
import com.example.data.model.SkillEntity
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsScreen(
    viewModel: WorkspaceViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val skills by viewModel.skills.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var skillName by remember { mutableStateOf("") }
    var skillDesc by remember { mutableStateOf("") }
    var skillInstructions by remember { mutableStateOf("") }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Build Custom Skill") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = skillName,
                        onValueChange = { skillName = it },
                        label = { Text("Skill Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = skillDesc,
                        onValueChange = { skillDesc = it },
                        label = { Text("Description") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = skillInstructions,
                        onValueChange = { skillInstructions = it },
                        label = { Text("System Instructions & Guidelines") },
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (skillName.isNotBlank() && skillInstructions.isNotBlank()) {
                            viewModel.createSkill(skillName, skillDesc, skillInstructions)
                            showCreateDialog = false
                            skillName = ""
                            skillDesc = ""
                            skillInstructions = ""
                            Toast.makeText(context, "Skill saved", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClaudeTerracotta)
                ) {
                    Text("Save Skill")
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
                title = { Text("Claude Custom Skills", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Skill", tint = ClaudeTerracotta)
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
                text = "Attached Prompt Capabilities (${skills.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(skills) { skill ->
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
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = ClaudeTerracotta)
                                    Text(
                                        text = skill.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                IconButton(onClick = { viewModel.deleteSkill(skill.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }

                            Text(
                                text = skill.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = skill.instructions,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(8.dp),
                                    maxLines = 4
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
