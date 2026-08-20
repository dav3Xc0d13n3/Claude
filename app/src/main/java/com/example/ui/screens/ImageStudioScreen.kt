package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.WorkspaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageStudioScreen(
    viewModel: WorkspaceViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val images by viewModel.generatedImages.collectAsState()

    var promptInput by remember { mutableStateOf("") }
    var selectedAspectRatio by remember { mutableStateOf("1:1") }
    var selectedStyle by remember { mutableStateOf("Cybernetic Design") }
    var isGenerating by remember { mutableStateOf(false) }

    val styles = listOf("Cybernetic Design", "3D Glassmorphism", "Futuristic AI", "Cinematic Render")
    val aspectRatios = listOf("1:1", "16:9", "9:16", "4:3")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ClaudeTerracotta)
                        Text("Image Generation Studio", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
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
            // Creative Prompt Panel
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Generative Diffusion Studio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        placeholder = { Text("Describe the visual asset to generate (e.g., Crystalline AI hub with warm glowing nodes)...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ClaudeTerracotta
                        )
                    )

                    // Styles & Aspect Ratio Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Aspect Ratio", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                aspectRatios.forEach { ratio ->
                                    FilterChip(
                                        selected = selectedAspectRatio == ratio,
                                        onClick = { selectedAspectRatio = ratio },
                                        label = { Text(ratio, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = ClaudeTerracotta.copy(alpha = 0.2f),
                                            selectedLabelColor = ClaudeTerracotta
                                        )
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (promptInput.isNotBlank()) {
                                    isGenerating = true
                                    viewModel.generateImage(promptInput, selectedStyle, selectedAspectRatio)
                                    Toast.makeText(context, "Generating image with Gemini 2.5 Flash Image...", Toast.LENGTH_SHORT).show()
                                    isGenerating = false
                                } else {
                                    Toast.makeText(context, "Please enter a prompt first!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ClaudeTerracotta),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Generate")
                        }
                    }
                }
            }

            Text("Midjourney-Style Gallery (${images.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            // Grid Gallery View
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(images) { img ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Box {
                            AsyncImage(
                                model = img.imageUrl,
                                contentDescription = img.prompt,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Overlay gradient & info
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter),
                                color = Color.Black.copy(alpha = 0.7f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        img.prompt,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { viewModel.deleteGeneratedImage(img.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
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
