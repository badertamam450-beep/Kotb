package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BookThemeMode
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary

enum class ArabicFontOption(val displayName: String, val fontFamily: FontFamily) {
    SERIF("نسخ كلاسيكي / أميري", FontFamily.Serif),
    DEFAULT("خط النظام الواضح", FontFamily.Default),
    MONOSPACE("خط خطي مميز", FontFamily.Monospace)
}

@Composable
fun ReadingSettingsDialog(
    currentTheme: BookThemeMode,
    currentFontSize: Float,
    currentFontOption: ArabicFontOption,
    isPagedMode: Boolean,
    onThemeChanged: (BookThemeMode) -> Unit,
    onFontSizeChanged: (Float) -> Unit,
    onFontOptionChanged: (ArabicFontOption) -> Unit,
    onModeChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(GoldLight, GoldDark)),
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "خيارات القراءة والتصميم",
                        style = MaterialTheme.typography.titleLarge,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(color = GoldPrimary.copy(alpha = 0.3f))

                // 1. Reading Mode (Paged vs Continuous Scroll)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "نمط العرض والقراءة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onModeChanged(true) },
                            modifier = Modifier.weight(1f).testTag("mode_paged_button"),
                            colors = if (isPagedMode) ButtonDefaults.outlinedButtonColors(
                                containerColor = GoldPrimary.copy(alpha = 0.15f)
                            ) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text(
                                text = "صفحات الكتاب (١٦)",
                                color = if (isPagedMode) GoldPrimary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isPagedMode) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { onModeChanged(false) },
                            modifier = Modifier.weight(1f).testTag("mode_scroll_button"),
                            colors = if (!isPagedMode) ButtonDefaults.outlinedButtonColors(
                                containerColor = GoldPrimary.copy(alpha = 0.15f)
                            ) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text(
                                text = "قراءة متصلة",
                                color = if (!isPagedMode) GoldPrimary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (!isPagedMode) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // 2. Font Size Slider
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "حجم الخط",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${currentFontSize.toInt()} نقطة",
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = currentFontSize,
                        onValueChange = onFontSizeChanged,
                        valueRange = 14f..28f,
                        steps = 6,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldPrimary,
                            activeTrackColor = GoldPrimary,
                            inactiveTrackColor = GoldDark.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("font_size_slider")
                    )
                }

                // 3. Theme Selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "المظهر الإيماني",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BookThemeMode.values().forEach { mode ->
                            val isSelected = currentTheme == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when (mode) {
                                            BookThemeMode.MIDNIGHT_GOLD -> Color(0xFF0A0F1D)
                                            BookThemeMode.ROYAL_EMERALD -> Color(0xFF06281E)
                                            BookThemeMode.WARM_PARCHMENT -> Color(0xFFFBF8EE)
                                            BookThemeMode.PURE_DARK -> Color(0xFF141414)
                                        }
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 0.8.dp,
                                        color = if (isSelected) GoldPrimary else Color.Gray.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onThemeChanged(mode) }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "محدد",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().testTag("close_settings_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("حفظ والعودة للقراءة", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
