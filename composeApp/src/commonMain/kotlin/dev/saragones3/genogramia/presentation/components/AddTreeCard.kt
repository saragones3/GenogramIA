package dev.saragones3.genogramia.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary

@Composable
fun AddTreeCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = Color(0xFFB2E0E0)
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFFF0FDFA))
                .drawBehind {
                    val stroke =
                        Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f),
                        )
                    drawRoundRect(
                        color = borderColor,
                        style = stroke,
                        cornerRadius = CornerRadius(32.dp.toPx()),
                    )
                }.clickable { onClick() }
                .padding(vertical = 32.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier =
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Primary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                lineHeight = 20.sp,
            )
        }
    }
}

@Preview
@Composable
private fun AddTreeCardPreview() {
    GenogramiaTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AddTreeCard(
                title = "Start New Tree",
                subtitle = "Begin a new journey into a different branch of your heritage.",
                onClick = {},
            )
        }
    }
}
