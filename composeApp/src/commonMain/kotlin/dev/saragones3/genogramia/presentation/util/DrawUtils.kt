package dev.saragones3.genogramia.presentation.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun DrawScope.drawZigzag(
    start: Offset,
    end: Offset,
    lineWidth: Float = 1.5f,
    color: Color = Color.Black,
) {
    val direction = end - start
    val length = direction.getDistance()
    if (length < 1f) return
    val unit = direction / length
    val normal = Offset(-unit.y, unit.x)
    val wavelength = 10.dp.toPx()
    val amplitude = 6.dp.toPx()
    val steps = (length / wavelength).toInt()

    val path = Path()
    path.moveTo(start.x, start.y)
    for (i in 0..steps) {
        val nextP = if (i < steps) start + direction * ((i + 0.5f) / steps) else null

        if (nextP != null) {
            val side = if (i % 2 == 0) 1f else -1f
            val anchor = nextP + normal * (amplitude * side)
            path.lineTo(anchor.x, anchor.y)
        } else {
            path.lineTo(end.x, end.y)
        }
    }
    drawPath(path, color, style = Stroke(width = lineWidth))
}

fun DrawScope.drawArrowHead(
    point: Offset,
    direction: Offset,
    size: Float = 12.dp.toPx(),
    fill: Boolean = false,
    color: Color = Color.Black,
    strokeWidth: Float = 2f,
) {
    val dist = direction.getDistance()
    if (dist < 1f) return
    val unit = direction / dist
    val normal = Offset(-unit.y, unit.x)
    val p1 = point - unit * size + normal * (size / 2)
    val p2 = point - unit * size - normal * (size / 2)

    val path =
        Path().apply {
            moveTo(point.x, point.y)
            lineTo(p1.x, p1.y)
            lineTo(p2.x, p2.y)
            close()
        }
    if (fill) {
        drawPath(path, color)
    } else {
        drawPath(path, color, style = Stroke(width = strokeWidth))
    }
}

fun DrawScope.drawDeathMark(
    color: Color = Color.Black,
    strokeWidth: Dp = 1.dp,
) {
    drawLine(
        color = color,
        start = Offset.Zero,
        end = Offset(size.width, size.height),
        strokeWidth = strokeWidth.toPx(),
    )
    drawLine(
        color = color,
        start = Offset(size.width, 0f),
        end = Offset(0f, size.height),
        strokeWidth = strokeWidth.toPx(),
    )
}

fun DrawScope.drawSexualOrientationMark(
    nodeSize: Float,
    color: Color = Color.Black,
    strokeWidth: Dp = 1.dp,
) {
    val trianglePath =
        Path().apply {
            val triangleSize = nodeSize * 0.7f
            moveTo(center.x - triangleSize / 2, center.y - triangleSize / 3)
            lineTo(center.x + triangleSize / 2, center.y - triangleSize / 3)
            lineTo(center.x, center.y + triangleSize * 2 / 3)
            close()
        }
    drawPath(
        path = trianglePath,
        color = color,
        style = Stroke(width = strokeWidth.toPx()),
    )
}

fun DrawScope.drawSlashes(
    center: Offset,
    count: Int,
    direction: Offset,
    isReconciliation: Boolean = false,
    color: Color = Color.Gray,
    strokeWidth: Float = 1.5.dp.toPx(),
) {
    val length = direction.getDistance()
    if (length < 1f) return
    val unit = direction / length
    val normal = Offset(-unit.y, unit.x)
    val slashLength = 14.dp.toPx()
    val spacing = 6.dp.toPx()

    // Slash direction: bottom-left to top-right (/) for a left-to-right line
    val slashDir = (unit - normal)
    val slashUnit = slashDir / slashDir.getDistance()

    for (i in 0 until count) {
        val offsetFactor = (i - (count - 1) / 2f)
        val slashCenter = center + unit * (offsetFactor * spacing)
        drawLine(
            color = color,
            start = slashCenter - slashUnit * (slashLength / 2),
            end = slashCenter + slashUnit * (slashLength / 2),
            strokeWidth = strokeWidth,
        )
    }

    if (isReconciliation) {
        // Draw an X (top-left to bottom-right slash \)
        val crossSlashDir = (unit + normal)
        val crossSlashUnit = crossSlashDir / crossSlashDir.getDistance()
        drawLine(
            color = color,
            start = center - crossSlashUnit * (slashLength / 2),
            end = center + crossSlashUnit * (slashLength / 2),
            strokeWidth = strokeWidth,
        )
    }
}

@Composable
fun Modifier.maleNode(
    size: Dp,
    isSelected: Boolean,
    isIndex: Boolean,
) = this.personNode(
    shape = RoundedCornerShape(12.dp),
    isSelected = isSelected,
    isIndex = isIndex,
    color = Color(0xFFD1E4FF),
    size = size,
)

@Composable
fun Modifier.femaleNode(
    size: Dp,
    isSelected: Boolean,
    isIndex: Boolean,
) = this.personNode(
    shape = CircleShape,
    isSelected = isSelected,
    isIndex = isIndex,
    color = Color(0xFFFFD1DC),
    size = size,
)

@Composable
private fun Modifier.personNode(
    shape: Shape,
    isSelected: Boolean,
    isIndex: Boolean,
    color: Color,
    size: Dp,
) = this
    .nodeBorder(shape, isSelected, isIndex)
    .background(color, shape)
    .size(size)
    .padding(4.dp)

@Composable
private fun Modifier.nodeBorder(
    shape: Shape,
    isSelected: Boolean,
    isIndex: Boolean,
) = this
    .then(
        if (isSelected) {
            Modifier.border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = shape,
            )
        } else {
            Modifier
        },
    ).padding(4.dp)
    .then(
        if (isIndex) {
            Modifier.border(shape).padding(4.dp)
        } else {
            Modifier
        },
    ).border(shape)

@Composable
private fun Modifier.border(shape: Shape) =
    border(
        width = 1.dp,
        color = Color.Black,
        shape = shape,
    )
