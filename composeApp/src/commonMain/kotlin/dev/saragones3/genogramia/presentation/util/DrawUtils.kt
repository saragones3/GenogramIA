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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.saragones3.genogramia.domain.model.Relationship

fun DrawScope.drawStructuralRelationshipLine(
    p1Center: Offset,
    p2Center: Offset,
    nodeSize: Float,
    type: Relationship.RelationshipType,
    dateText: String = "",
    textMeasurer: TextMeasurer? = null,
    labelStyle: TextStyle? = null,
    color: Color = Color(0xFFBDBDBD),
    strokeWidth: Float = 1.5.dp.toPx(),
) {
    val verticalGap = 24.dp.toPx()
    val p1Bottom = Offset(p1Center.x, p1Center.y + (nodeSize / 2))
    val p2Bottom = Offset(p2Center.x, p2Center.y + (nodeSize / 2))

    val start = Offset(p1Bottom.x, p1Bottom.y + verticalGap)
    val end = Offset(p2Bottom.x, p2Bottom.y + verticalGap)

    // Draw vertical stems from nodes
    drawLine(color, p1Bottom, start, strokeWidth)
    drawLine(color, p2Bottom, end, strokeWidth)

    val pathEffect =
        if (type == Relationship.RelationshipType.COHABITATION) {
            PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        } else {
            null
        }

    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        pathEffect = pathEffect,
    )

    // Draw structural slashes
    val direction = end - start
    when (type) {
        Relationship.RelationshipType.SEPARATION -> {
            drawSlashes(
                center = (start + end) / 2f,
                count = 1,
                direction = direction,
                color = color,
                strokeWidth = strokeWidth,
            )
        }

        Relationship.RelationshipType.DIVORCE -> {
            drawSlashes(
                center = (start + end) / 2f,
                count = 2,
                direction = direction,
                color = color,
                strokeWidth = strokeWidth,
            )
        }

        Relationship.RelationshipType.RECONCILIATION -> {
            drawSlashes(
                center = (start + end) / 2f,
                count = 2,
                direction = direction,
                isReconciliation = true,
                color = color,
                strokeWidth = strokeWidth,
            )
        }

        else -> {}
    }

    // Draw date text
    if (dateText.isNotEmpty() && textMeasurer != null && labelStyle != null) {
        val textLayoutResult = textMeasurer.measure(dateText, labelStyle)
        val textWidth = textLayoutResult.size.width
        val midPoint = (start + end) / 2f

        drawText(
            textLayoutResult,
            topLeft =
                Offset(
                    midPoint.x - textWidth / 2f,
                    midPoint.y + 4.dp.toPx(), // Position text below the horizontal line
                ),
        )
    }
}

fun DrawScope.drawEmotionalBondLine(
    p1Center: Offset,
    p2Center: Offset,
    nodeSize: Float,
    bond: Relationship.EmotionalBond,
    color: Color = Color(0xFFBDBDBD),
    strokeWidth: Float = 1.5.dp.toPx(),
) {
    val p1IsLeft = p1Center.x < p2Center.x
    val start =
        Offset(
            if (p1IsLeft) p1Center.x + nodeSize / 2 else p1Center.x - nodeSize / 2,
            p1Center.y,
        )
    val end =
        Offset(
            if (p1IsLeft) p2Center.x - nodeSize / 2 else p2Center.x + nodeSize / 2,
            p2Center.y,
        )

    val direction = end - start
    val length = direction.getDistance()
    if (length < 1f) return
    val unit = direction / length
    val normal = Offset(-unit.y, unit.x)

    when (bond) {
        Relationship.EmotionalBond.POSITIVE -> {
            drawLine(color, start, end, strokeWidth)
        }

        Relationship.EmotionalBond.DISTANT -> {
            drawLine(
                color = color,
                start = start,
                end = end,
                strokeWidth = strokeWidth,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
            )
        }

        Relationship.EmotionalBond.INTIMATE -> {
            val offset = normal * 8.dp.toPx()
            drawLine(color, start + offset, end + offset, strokeWidth)
            drawLine(color, start - offset, end - offset, strokeWidth)
        }

        Relationship.EmotionalBond.FUSED -> {
            val offset = normal * 8.dp.toPx()
            drawLine(color, start + offset, end + offset, strokeWidth)
            drawLine(color, start, end, strokeWidth)
            drawLine(color, start - offset, end - offset, strokeWidth)
        }

        Relationship.EmotionalBond.CONFLICTUAL -> {
            drawZigzag(start, end, strokeWidth, color)
        }

        Relationship.EmotionalBond.INTIMATE_CONFLICTUAL -> {
            val offset = normal * 8.dp.toPx()
            drawLine(color, start + offset, end + offset, strokeWidth)
            drawLine(color, start - offset, end - offset, strokeWidth)
            drawZigzag(start, end, strokeWidth, color)
        }

        Relationship.EmotionalBond.FUSED_CONFLICTUAL -> {
            val offset = normal * 8.dp.toPx()
            drawLine(color, start + offset, end + offset, strokeWidth)
            drawLine(color, start, end, strokeWidth)
            drawLine(color, start - offset, end - offset, strokeWidth)
            drawZigzag(start, end, strokeWidth, color)
        }

        Relationship.EmotionalBond.FOCUSED -> {
            val arrowSize = 12.dp.toPx()
            val lineEnd = end - unit * arrowSize
            drawLine(color, start, lineEnd, strokeWidth)
            drawArrowHead(end, direction, color = color, strokeWidth = strokeWidth)
        }

        Relationship.EmotionalBond.RUPTURE -> {
            val mid = (start + end) / 2f
            val gap = 15.dp.toPx()
            // Draw the two segments of the line
            drawLine(color, start, mid - unit * (gap / 2), strokeWidth)
            drawLine(color, mid + unit * (gap / 2), end, strokeWidth)

            // We draw two perpendicular bars to indicate the rupture
            drawLine(
                color = color,
                start = mid - unit * (gap / 2) + normal * 10.dp.toPx(),
                end = mid - unit * (gap / 2) - normal * 10.dp.toPx(),
                strokeWidth = 2.dp.toPx(),
            )
            drawLine(
                color = color,
                start = mid + unit * (gap / 2) + normal * 10.dp.toPx(),
                end = mid + unit * (gap / 2) - normal * 10.dp.toPx(),
                strokeWidth = 2.dp.toPx(),
            )
        }

        Relationship.EmotionalBond.DIRECT_CONFLICTUAL -> {
            val arrowSize = 12.dp.toPx()
            val baseEnd = end - unit * arrowSize
            drawZigzag(start, baseEnd, strokeWidth, color)
            drawArrowHead(end, direction, color = color, strokeWidth = strokeWidth)
        }

        Relationship.EmotionalBond.ABUSE -> {
            val arrowSize = 12.dp.toPx()
            val baseEnd = end - unit * arrowSize
            drawZigzag(start, baseEnd, strokeWidth, color)
            drawArrowHead(end, direction, fill = true, color = color)
        }
    }
}

fun DrawScope.drawZigzag(
    start: Offset,
    end: Offset,
    lineWidth: Float = 1.5.dp.toPx(),
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
    strokeWidth: Float = 2.dp.toPx(),
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
