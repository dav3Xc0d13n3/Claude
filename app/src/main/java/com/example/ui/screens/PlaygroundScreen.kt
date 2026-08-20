package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.data.model.ProviderType
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.NavigationScreen
import com.example.ui.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundScreen(
    viewModel: WorkspaceViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(0) } // 0: Image Gen, 1: Voice Playground
    var imagePrompt by remember { mutableStateOf("") }
    var isGeneratingImage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image & Voice Playground", fontWeight = FontWeight.Bold) },
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
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = ClaudeTerracotta
                    )
                }
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Image Generation", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Image, contentDescription = null) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Voice Conversation", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Mic, contentDescription = null) }
                )
            }

            if (activeTab == 0) {
                // Image Generation View
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Gemini 2.5 Flash Image Generator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = imagePrompt,
                            onValueChange = { imagePrompt = it },
                            placeholder = { Text("Describe image (e.g., A sleek cybernetic panther on a dark obsidian neon pedestal)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (imagePrompt.isNotBlank()) {
                                    isGeneratingImage = true
                                    viewModel.createNewChat("Image Generation Task")
                                    viewModel.sendMessage("Generate Image with prompt: $imagePrompt")
                                    viewModel.navigateTo(NavigationScreen.CHAT)
                                    Toast.makeText(context, "Rendering image via Gemini Flash Image...", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = ClaudeTerracotta),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Image in Chat")
                        }
                    }
                }
            } else {
                // Voice Playground
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Real-Time Natural Voice Agent",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .border(2.dp, ClaudeTerracotta, RoundedCornerShape(50.dp)),
                            color = ClaudeTerracotta.copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Input",
                                    tint = ClaudeTerracotta,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.speakText("Welcome to Claude AI Assistant Workspace. Voice feedback engine is live and ready.")
                                Toast.makeText(context, "Speaking audio sample...", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ClaudeTerracotta)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Test Audio Speech Synthesis")
                        }
                    }
                }
            }
        }
    }
}
