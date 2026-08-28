package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary

@Composable
fun AudioPlayerBar(
    isPlaying: Boolean,
    isSpeakingAvailable: Boolean,
    currentPage: Int,
    totalPages: Int,
    speechRate: Float,
    onTogglePlay: () -> Unit,
    onNextPage: () -> Unit,
    onPrevPage: () -> Unit,
    onChangeSpeed: (Float) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSpeedMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(GoldPrimary.copy(alpha = 0.6f), GoldDark.copy(alpha = 0.3f), GoldPrimary.copy(alpha = 0.6f))
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.96f),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "القارئ الصوتي",
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isPlaying) "جاري القراءة الصوتية (صفحة $currentPage)..." else "القارئ الصوتي (صفحة $currentPage)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(32.dp).testTag("close_audio_bar")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق مشغل الصوت",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Player Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed selector button
                Box {
                    Button(
                        onClick = { showSpeedMenu = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = GoldLight
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(34.dp).testTag("speech_rate_button")
                    ) {
                        Text(
                            text = "${speechRate}x",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    DropdownMenu(
                        expanded = showSpeedMenu,
                        onDismissRequest = { showSpeedMenu = false }
                    ) {
                        listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { rate ->
                            DropdownMenuItem(
                                text = { Text("${rate}x السرعة", fontWeight = if (speechRate == rate) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    onChangeSpeed(rate)
                                    showSpeedMenu = false
                                }
                            )
                        }
                    }
                }

                // Middle Playback Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPrevPage,
                        enabled = currentPage > 1,
                        modifier = Modifier.size(36.dp).testTag("prev_page_audio")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "الصفحة السابقة",
                            tint = if (currentPage > 1) GoldLight else Color.Gray
                        )
                    }

                    // Main Play/Pause circular button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(GoldLight, GoldDark)
                                )
                            )
                            .clickable(onClick = onTogglePlay)
                            .testTag("play_pause_audio_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "إيقاف مؤقت" else "تشغيل القراءة",
                            tint = Color(0xFF1B1B1B),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    IconButton(
                        onClick = onNextPage,
                        enabled = currentPage < totalPages,
                        modifier = Modifier.size(36.dp).testTag("next_page_audio")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "الصفحة التالية",
                            tint = if (currentPage < totalPages) GoldLight else Color.Gray
                        )
                    }
                }

                // Progress Indicator text
                Text(
                    text = "$currentPage / $totalPages",
                    style = MaterialTheme.typography.labelMedium,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
