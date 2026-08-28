package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary

@Composable
fun IslamicPageHeader(
    chapterTitle: String,
    pageNumber: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book Title in Calligraphy
            Text(
                text = "كتاب إلى المفازة العظمى",
                style = MaterialTheme.typography.labelLarge,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )

            // Page Number in rosette badge
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(GoldPrimary.copy(alpha = 0.35f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
                    .border(1.2.dp, GoldPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$pageNumber",
                    style = MaterialTheme.typography.labelLarge,
                    color = GoldLight,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Arabesque Divider with Star
        ArabesqueDivider()
    }
}

@Composable
fun ArabesqueDivider(
    modifier: Modifier = Modifier,
    color: Color = GoldPrimary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, color.copy(alpha = 0.7f))
                    )
                )
        )
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "زخرفة إسلامية",
            tint = color,
            modifier = Modifier
                .size(16.dp)
                .padding(horizontal = 2.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(color.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
        )
    }
}

@Composable
fun BasmalaBanner(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        GoldPrimary.copy(alpha = 0.12f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            style = MaterialTheme.typography.titleLarge,
            color = GoldLight,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}

@Composable
fun IslamicPageBorder(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GoldPrimary.copy(alpha = 0.8f),
                        GoldDark.copy(alpha = 0.4f),
                        GoldLight.copy(alpha = 0.9f),
                        GoldPrimary.copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        // Subtle corner accents canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cornerLen = 24.dp.toPx()
            val strokeW = 2.dp.toPx()
            val goldColor = GoldPrimary.copy(alpha = 0.7f)

            // Top-Left Corner
            val pathTL = Path().apply {
                moveTo(8.dp.toPx(), 8.dp.toPx() + cornerLen)
                lineTo(8.dp.toPx(), 8.dp.toPx())
                lineTo(8.dp.toPx() + cornerLen, 8.dp.toPx())
            }
            drawPath(pathTL, goldColor, style = Stroke(width = strokeW))

            // Top-Right Corner
            val pathTR = Path().apply {
                moveTo(size.width - 8.dp.toPx() - cornerLen, 8.dp.toPx())
                lineTo(size.width - 8.dp.toPx(), 8.dp.toPx())
                lineTo(size.width - 8.dp.toPx(), 8.dp.toPx() + cornerLen)
            }
            drawPath(pathTR, goldColor, style = Stroke(width = strokeW))

            // Bottom-Left Corner
            val pathBL = Path().apply {
                moveTo(8.dp.toPx(), size.height - 8.dp.toPx() - cornerLen)
                lineTo(8.dp.toPx(), size.height - 8.dp.toPx())
                lineTo(8.dp.toPx() + cornerLen, size.height - 8.dp.toPx())
            }
            drawPath(pathBL, goldColor, style = Stroke(width = strokeW))

            // Bottom-Right Corner
            val pathBR = Path().apply {
                moveTo(size.width - 8.dp.toPx() - cornerLen, size.height - 8.dp.toPx())
                lineTo(size.width - 8.dp.toPx(), size.height - 8.dp.toPx())
                lineTo(size.width - 8.dp.toPx(), size.height - 8.dp.toPx() - cornerLen)
            }
            drawPath(pathBR, goldColor, style = Stroke(width = strokeW))
        }

        content()
    }
}

@Composable
fun GoldenLanternOrnament(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height * 0.45f)
        val radius = size.width * 0.35f
        
        // Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(GoldLight.copy(alpha = 0.5f), Color.Transparent),
                center = center,
                radius = radius * 1.5f
            ),
            center = center,
            radius = radius * 1.5f
        )

        // Hanging string
        drawLine(
            color = GoldPrimary,
            start = Offset(size.width / 2f, 0f),
            end = Offset(size.width / 2f, center.y - radius),
            strokeWidth = 2.dp.toPx()
        )

        // Lantern body
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(GoldLight, GoldDark)
            ),
            center = center,
            radius = radius
        )
    }
}
