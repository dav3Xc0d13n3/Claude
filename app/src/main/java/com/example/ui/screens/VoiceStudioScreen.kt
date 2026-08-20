package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClaudeTerracotta
import com.example.ui.viewmodel.WorkspaceViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceStudioScreen(
    viewModel: WorkspaceViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    var isListening by remember { mutableStateOf(false) }
    var transcriptText by remember { mutableStateOf("Listening for speech... Say 'Hey Claude, refactor my Android ViewModel'...") }

    // Waveform Animation state
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val waveAnimPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = ClaudeTerracotta)
                        Text("Futuristic Voice Assistant", fontWeight = FontWeight.Bold)
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Live Waveform Canvas Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, ClaudeTerracotta.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                        val barCount = 24
                        val barWidth = size.width / (barCount * 1.5f)
                        val maxBarHeight = size.height * 0.8f

                        for (i in 0 until barCount) {
                            val factor = if (isListening || isSpeaking) {
                                (kotlin.math.sin(waveAnimPhase * 3.14159f + i * 0.5f) + 1f) / 2f
                            } else {
                                0.2f
                            }
                            val barHeight = maxBarHeight * factor
                            val x = i * (barWidth * 1.5f)
                            val y = (size.height - barHeight) / 2f

                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(ClaudeTerracotta, Color(0xFFE5A84B))
                                ),
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.padding(12.dp).align(Alignment.TopEnd),
                        shape = RoundedCornerShape(6.dp),
                        color = ClaudeTerracotta.copy(alpha = 0.2f)
                    ) {
                        Text(
                            if (isListening) "LIVE MIC STREAM" else if (isSpeaking) "TTS AUDIO OUTPUT" else "STANDBY",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = ClaudeTerracotta,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Real-time Transcription Box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Live Transcription", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        transcriptText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Glowing Mic Control
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .clickable {
                            isListening = !isListening
                            if (isListening) {
                                transcriptText = "Recording audio... 'Show me the active NVIDIA NIM models'..."
                                Toast.makeText(context, "Listening started!", Toast.LENGTH_SHORT).show()
                            } else {
                                transcriptText = "Speech recognized! Query sent to Claude workspace."
                            }
                        },
                    color = if (isListening) ClaudeTerracotta else MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                            contentDescription = "Mic",
                            tint = if (isListening) Color.White else ClaudeTerracotta,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Text(
                    if (isListening) "Tap to stop listening" else "Tap microphone to speak",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
