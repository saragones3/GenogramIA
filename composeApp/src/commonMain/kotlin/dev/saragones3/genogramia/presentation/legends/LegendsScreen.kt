package dev.saragones3.genogramia.presentation.legends

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.saragones3.genogramia.presentation.util.drawArrowHead
import dev.saragones3.genogramia.presentation.util.drawDeathMark
import dev.saragones3.genogramia.presentation.util.drawSexualOrientationMark
import dev.saragones3.genogramia.presentation.util.drawSlashes
import dev.saragones3.genogramia.presentation.util.drawZigzag
import dev.saragones3.genogramia.presentation.util.femaleNode
import dev.saragones3.genogramia.presentation.util.maleNode
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary
import dev.saragones3.genogramia.ui.theme.Surface
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.emotional_bond_abuse
import genogramia.composeapp.generated.resources.emotional_bond_conflictual
import genogramia.composeapp.generated.resources.emotional_bond_direct_conflictual
import genogramia.composeapp.generated.resources.emotional_bond_distant
import genogramia.composeapp.generated.resources.emotional_bond_focused
import genogramia.composeapp.generated.resources.emotional_bond_fused
import genogramia.composeapp.generated.resources.emotional_bond_fused_conflictual
import genogramia.composeapp.generated.resources.emotional_bond_intimate
import genogramia.composeapp.generated.resources.emotional_bond_intimate_conflictual
import genogramia.composeapp.generated.resources.emotional_bond_positive
import genogramia.composeapp.generated.resources.emotional_bond_rupture
import genogramia.composeapp.generated.resources.legends_rel_adoption_desc
import genogramia.composeapp.generated.resources.legends_rel_cohabitation_desc
import genogramia.composeapp.generated.resources.legends_rel_divorce_desc
import genogramia.composeapp.generated.resources.legends_rel_fraternal_twins_desc
import genogramia.composeapp.generated.resources.legends_rel_identical_twins_desc
import genogramia.composeapp.generated.resources.legends_rel_marriage_desc
import genogramia.composeapp.generated.resources.legends_rel_offspring_desc
import genogramia.composeapp.generated.resources.legends_rel_reconciliation_desc
import genogramia.composeapp.generated.resources.legends_rel_separation_desc
import genogramia.composeapp.generated.resources.legends_section_basic
import genogramia.composeapp.generated.resources.legends_section_emotional
import genogramia.composeapp.generated.resources.legends_section_relationships
import genogramia.composeapp.generated.resources.legends_symbol_alive
import genogramia.composeapp.generated.resources.legends_symbol_deceased
import genogramia.composeapp.generated.resources.legends_symbol_female
import genogramia.composeapp.generated.resources.legends_symbol_male
import genogramia.composeapp.generated.resources.legends_symbol_orientation
import genogramia.composeapp.generated.resources.legends_symbol_patient
import genogramia.composeapp.generated.resources.legends_vocabulary_description
import genogramia.composeapp.generated.resources.legends_vocabulary_title
import genogramia.composeapp.generated.resources.relationship_adoption
import genogramia.composeapp.generated.resources.relationship_cohabitation
import genogramia.composeapp.generated.resources.relationship_divorce
import genogramia.composeapp.generated.resources.relationship_fraternal_twin
import genogramia.composeapp.generated.resources.relationship_identical_twin
import genogramia.composeapp.generated.resources.relationship_marriage
import genogramia.composeapp.generated.resources.relationship_offspring
import genogramia.composeapp.generated.resources.relationship_reconciliation
import genogramia.composeapp.generated.resources.relationship_separation
import org.jetbrains.compose.resources.stringResource

@Composable
fun LegendsScreen() {
    LegendsContent()
}

@Composable
private fun LegendsContent() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                HeaderSection()
            }
            item {
                BasicSymbolsSection()
            }
            item {
                RelationshipsSection()
            }
            item {
                EmotionalBondsSection()
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Column(modifier = Modifier.padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)) {
        Text(
            text = stringResource(Res.string.legends_vocabulary_title),
            style = MaterialTheme.typography.displaySmall,
            color = Primary,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 36.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.legends_vocabulary_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    color: Color = Primary,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 16.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .width(24.dp)
                    .height(4.dp)
                    .background(color, RoundedCornerShape(2.dp)),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun SectionWrapper(
    title: String,
    titleColor: Color,
    containerColor: Color = Color(0xFFF1F3F5),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(title = title, color = titleColor)
            content()
        }
    }
}

@Composable
private fun BasicSymbolsSection() {
    SectionWrapper(
        title = stringResource(Res.string.legends_section_basic),
        titleColor = Color(0xFF136299),
    ) {
        GenderSymbols()
        Spacer(modifier = Modifier.height(16.dp))
        StatusSymbols()
        Spacer(modifier = Modifier.height(16.dp))
        OtherSymbols()
    }
}

@Composable
private fun GenderSymbols() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SymbolCard(
            modifier = Modifier.weight(1f),
            label = stringResource(Res.string.legends_symbol_male),
        ) {
            Box(modifier = Modifier.maleNode(size = 48.dp, isSelected = false, isIndex = false))
        }
        SymbolCard(
            modifier = Modifier.weight(1f),
            label = stringResource(Res.string.legends_symbol_female),
        ) {
            Box(modifier = Modifier.femaleNode(size = 48.dp, isSelected = false, isIndex = false))
        }
    }
}

@Composable
private fun StatusSymbols() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SymbolCard(
            modifier = Modifier.weight(1f),
            label = stringResource(Res.string.legends_symbol_alive),
            topContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "2001",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    )
                }
            },
        ) {
            Box(modifier = Modifier.femaleNode(size = 48.dp, isSelected = false, isIndex = false))
        }
        SymbolCard(
            modifier = Modifier.weight(1f),
            label = stringResource(Res.string.legends_symbol_deceased),
            topContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "1936",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    )
                    Text(text = "-", modifier = Modifier)
                    Text(
                        text = "2022",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    )
                }
            },
        ) {
            Box(modifier = Modifier.maleNode(size = 48.dp, isSelected = false, isIndex = false)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawDeathMark()
                }
            }
        }
    }
}

@Composable
private fun OtherSymbols() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SymbolCard(
            modifier = Modifier.weight(1f),
            label = stringResource(Res.string.legends_symbol_patient),
        ) {
            Box(modifier = Modifier.maleNode(size = 48.dp, isSelected = false, isIndex = true))
        }
        SymbolCard(
            modifier = Modifier.fillMaxWidth(0.5f),
            label = stringResource(Res.string.legends_symbol_orientation),
        ) {
            Box(
                modifier = Modifier.femaleNode(size = 48.dp, isSelected = false, isIndex = false),
                contentAlignment = Alignment.Center,
            ) {
                val nodeSize = with(LocalDensity.current) { 48.dp.toPx() }
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawSexualOrientationMark(nodeSize)
                }
            }
        }
    }
}

@Composable
private fun SymbolCard(
    modifier: Modifier = Modifier,
    label: String,
    topContent: (@Composable () -> Unit)? = {},
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            topContent?.invoke()
            Box(modifier = Modifier.height(64.dp), contentAlignment = Alignment.Center) {
                content()
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun RelationshipsSection() {
    SectionWrapper(
        title = stringResource(Res.string.legends_section_relationships),
        titleColor = Color(0xFF006565),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MarriageItem()
            CohabitationItem()
            SeparationItem()
            DivorceItem()
            ReconciliationItem()
            OffspringItem()
            AdoptionItem()
            FraternalTwinsItem()
            IdenticalTwinsItem()
        }
    }
}

@Composable
private fun MarriageItem() {
    RelationshipItem(
        title = stringResource(Res.string.relationship_marriage),
        subtitle = stringResource(Res.string.legends_rel_marriage_desc),
    ) { start, end ->
        drawLineHelper(startX = start, endX = end)
    }
}

@Composable
private fun CohabitationItem() {
    RelationshipItem(
        title = stringResource(Res.string.relationship_cohabitation),
        subtitle = stringResource(Res.string.legends_rel_cohabitation_desc),
    ) { start, end ->
        drawLineHelper(
            startX = start,
            endX = end,
            isDotted = true,
        )
    }
}

@Composable
private fun SeparationItem() {
    RelationshipItem(
        title = stringResource(Res.string.relationship_separation),
        subtitle = stringResource(Res.string.legends_rel_separation_desc),
    ) { start, end ->
        drawLineHelper(
            startX = start,
            endX = end,
            isDashed = true,
        )
        drawSlashes(
            center = Offset(x = (start + end) / 2, y = size.height / 2),
            count = 1,
            direction = Offset(1f, 0f),
        )
    }
}

@Composable
private fun DivorceItem() {
    RelationshipItem(
        title = stringResource(Res.string.relationship_divorce),
        subtitle = stringResource(Res.string.legends_rel_divorce_desc),
    ) { start, end ->
        drawLineHelper(startX = start, endX = end)
        drawSlashes(
            center = Offset(x = (start + end) / 2, y = size.height / 2),
            count = 2,
            direction = Offset(1f, 0f),
        )
    }
}

@Composable
private fun ReconciliationItem() {
    RelationshipItem(
        title = stringResource(Res.string.relationship_reconciliation),
        subtitle = stringResource(Res.string.legends_rel_reconciliation_desc),
    ) { start, end ->
        drawLineHelper(startX = start, endX = end)
        drawSlashes(
            center = Offset((start + end) / 2, size.height / 2),
            count = 2,
            direction = Offset(1f, 0f),
            isReconciliation = true,
        )
    }
}

@Composable
private fun OffspringItem() {
    VerticalRelationshipItem(
        title = stringResource(Res.string.relationship_offspring),
        subtitle = stringResource(Res.string.legends_rel_offspring_desc),
    ) {
        val nodeSize = 16.dp.toPx()
        drawLine(
            color = Color.Gray,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height - nodeSize),
            strokeWidth = 1.5.dp.toPx(),
        )
    }
}

@Composable
private fun AdoptionItem() {
    VerticalRelationshipItem(
        title = stringResource(Res.string.relationship_adoption),
        subtitle = stringResource(Res.string.legends_rel_adoption_desc),
    ) {
        val nodeSize = 16.dp.toPx()
        drawLine(
            color = Color.Gray,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height - nodeSize),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
        )
    }
}

@Composable
private fun FraternalTwinsItem() {
    VerticalRelationshipItem(
        title = stringResource(Res.string.relationship_fraternal_twin),
        subtitle = stringResource(Res.string.legends_rel_fraternal_twins_desc),
        isTwin = true,
    ) {
        val nodeSize = 16.dp.toPx()
        val topCenter = Offset(size.width / 2, 0f)
        drawLine(
            color = Color.Gray,
            start = topCenter,
            end = Offset(nodeSize / 2, size.height - nodeSize),
            strokeWidth = 1.5.dp.toPx(),
        )
        drawLine(
            color = Color.Gray,
            start = topCenter,
            end = Offset(size.width - nodeSize / 2, size.height - nodeSize),
            strokeWidth = 1.5.dp.toPx(),
        )
    }
}

@Composable
private fun IdenticalTwinsItem() {
    VerticalRelationshipItem(
        title = stringResource(Res.string.relationship_identical_twin),
        subtitle = stringResource(Res.string.legends_rel_identical_twins_desc),
        isTwin = true,
    ) {
        val nodeSize = 16.dp.toPx()
        val topCenter = Offset(size.width / 2, 0f)
        val leftChildTop = Offset(nodeSize / 2, size.height - nodeSize)
        val rightChildTop = Offset(size.width - nodeSize / 2, size.height - nodeSize)
        drawLine(
            color = Color.Gray,
            start = topCenter,
            end = leftChildTop,
            strokeWidth = 1.5.dp.toPx(),
        )
        drawLine(
            color = Color.Gray,
            start = topCenter,
            end = rightChildTop,
            strokeWidth = 1.5.dp.toPx(),
        )
        // Bar connecting siblings
        val barY = topCenter.y + (size.height - nodeSize) * 0.5f
        val leftBarX = topCenter.x + (leftChildTop.x - topCenter.x) * 0.5f
        val rightBarX = topCenter.x + (rightChildTop.x - topCenter.x) * 0.5f
        drawLine(
            color = Color.Gray,
            start = Offset(leftBarX, barY),
            end = Offset(rightBarX, barY),
            strokeWidth = 1.5.dp.toPx(),
        )
    }
}

@Composable
private fun RelationshipItem(
    title: String,
    subtitle: String,
    drawAction: DrawScope.(Float, Float) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Canvas(modifier = Modifier.width(100.dp).height(24.dp)) {
                val nodeSize = 16.dp.toPx()
                val strokeWidth = 1.5.dp.toPx()
                val squareX = 0f
                val circleX = size.width - nodeSize

                drawRect(
                    color = Color(0xFF136299),
                    topLeft = Offset(squareX, (size.height - nodeSize) / 2),
                    size = Size(nodeSize, nodeSize),
                    style = Stroke(width = strokeWidth),
                )
                drawCircle(
                    color = Color(0xFF884364),
                    center = Offset(circleX + nodeSize / 2, size.height / 2),
                    radius = nodeSize / 2,
                    style = Stroke(width = strokeWidth),
                )
                drawAction(nodeSize, circleX)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
            }
        }
    }
}

@Composable
private fun VerticalRelationshipItem(
    title: String,
    subtitle: String,
    isTwin: Boolean = false,
    drawAction: DrawScope.() -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Canvas(modifier = Modifier.width(100.dp).height(48.dp)) {
                val nodeSize = 16.dp.toPx()
                val strokeWidth = 1.5.dp.toPx()
                // Parent line (horizontal)
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth,
                )

                drawAction()

                // Child nodes (bottom)
                if (isTwin) {
                    drawRect(
                        color = Color(0xFF136299),
                        topLeft = Offset(0f, size.height - nodeSize),
                        size = Size(nodeSize, nodeSize),
                        style = Stroke(width = strokeWidth),
                    )
                    drawCircle(
                        color = Color(0xFF884364),
                        center = Offset(size.width - nodeSize / 2, size.height - nodeSize / 2),
                        radius = nodeSize / 2,
                        style = Stroke(width = strokeWidth),
                    )
                } else {
                    drawRect(
                        color = Color(0xFF136299),
                        topLeft = Offset((size.width - nodeSize) / 2, size.height - nodeSize),
                        size = Size(nodeSize, nodeSize),
                        style = Stroke(width = strokeWidth),
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
            }
        }
    }
}

@Composable
private fun EmotionalBondsSection() {
    SectionWrapper(
        title = stringResource(Res.string.legends_section_emotional),
        titleColor = Surface,
        containerColor = Primary,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PositiveItem()
            DistantItem()
            IntimateItem()
            IntimateConflictualItem()
            FocusedItem()
            FusedItem()
            ConflictualItem()
            FusedConflictualItem()
            DirectConflictualItem()
            RuptureItem()
            AbuseItem()
        }
    }
}

@Composable
private fun PositiveItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_positive)) {
        drawLine(
            color = Color.White,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 2.dp.toPx(),
        )
    }
}

@Composable
private fun DistantItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_distant)) {
        drawLine(
            color = Color.White,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
        )
    }
}

@Composable
private fun IntimateItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_intimate)) {
        val y = size.height / 2
        drawLine(
            color = Color.White,
            start = Offset(0f, y - 2.dp.toPx()),
            end = Offset(size.width, y - 2.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
        )
        drawLine(
            color = Color.White,
            start = Offset(0f, y + 2.dp.toPx()),
            end = Offset(size.width, y + 2.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
        )
    }
}

@Composable
private fun IntimateConflictualItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_intimate_conflictual)) {
        val y = size.height / 2
        drawLine(
            color = Color.White,
            start = Offset(0f, y - 4.dp.toPx()),
            end = Offset(size.width, y - 4.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
        )
        drawLine(
            color = Color.White,
            start = Offset(0f, y + 4.dp.toPx()),
            end = Offset(size.width, y + 4.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
        )
        drawZigzag(
            start = Offset(0f, y),
            end = Offset(size.width, y),
            color = Color.White,
            lineWidth = 2.dp.toPx(),
        )
    }
}

@Composable
private fun FocusedItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_focused)) {
        val y = size.height / 2
        val arrowSize = 12.dp.toPx()
        val unit = Offset(1f, 0f)
        drawLine(
            color = Color.White,
            start = Offset(0f, y),
            end = Offset(size.width - arrowSize, y),
            strokeWidth = 2.dp.toPx(),
        )
        drawArrowHead(
            point = Offset(size.width, y),
            direction = unit,
            color = Color.White,
            strokeWidth = 2.dp.toPx(),
        )
    }
}

@Composable
private fun FusedItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_fused)) {
        val y = size.height / 2
        drawLine(
            color = Color.White,
            start = Offset(0f, y - 4.dp.toPx()),
            end = Offset(size.width, y - 4.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
        )
        drawLine(
            color = Color.White,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 2.dp.toPx(),
        )
        drawLine(
            color = Color.White,
            start = Offset(0f, y + 4.dp.toPx()),
            end = Offset(size.width, y + 4.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
        )
    }
}

@Composable
private fun ConflictualItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_conflictual)) {
        drawZigzag(
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            color = Color.White,
            lineWidth = 2.dp.toPx(),
        )
    }
}

@Composable
private fun FusedConflictualItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_fused_conflictual)) {
        val y = size.height / 2
        drawLine(
            color = Color.White,
            start = Offset(0f, y - 5.dp.toPx()),
            end = Offset(size.width, y - 5.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
        )
        drawLine(
            color = Color.White,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 2.dp.toPx(),
        )
        drawLine(
            color = Color.White,
            start = Offset(0f, y + 5.dp.toPx()),
            end = Offset(size.width, y + 5.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
        )
        drawZigzag(
            start = Offset(0f, y),
            end = Offset(size.width, y),
            color = Color.White,
            lineWidth = 2.dp.toPx(),
        )
    }
}

@Composable
private fun DirectConflictualItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_direct_conflictual)) {
        val y = size.height / 2
        val arrowSize = 12.dp.toPx()
        val start = Offset(0f, y)
        val end = Offset(size.width, y)
        val unit = Offset(1f, 0f)
        val arrowBase = end - unit * arrowSize
        drawZigzag(
            start = start,
            end = arrowBase,
            color = Color.White,
            lineWidth = 2.dp.toPx(),
        )
        drawArrowHead(
            point = end,
            direction = unit,
            color = Color.White,
            strokeWidth = 2.dp.toPx(),
        )
    }
}

@Composable
private fun RuptureItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_rupture)) {
        val y = size.height / 2
        val mid = size.width / 2
        val gap = 12.dp.toPx()
        drawLine(
            color = Color.White,
            start = Offset(0f, y),
            end = Offset(mid - gap / 2, y),
            strokeWidth = 2.dp.toPx(),
        )
        drawLine(
            color = Color.White,
            start = Offset(mid + gap / 2, y),
            end = Offset(size.width, y),
            strokeWidth = 2.dp.toPx(),
        )
        drawLine(
            color = Color.White,
            start = Offset(mid - gap / 2, y - 8.dp.toPx()),
            end = Offset(mid - gap / 2, y + 8.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
        )
        drawLine(
            color = Color.White,
            start = Offset(mid + gap / 2, y - 8.dp.toPx()),
            end = Offset(mid + gap / 2, y + 8.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
        )
    }
}

@Composable
private fun AbuseItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_abuse)) {
        val y = size.height / 2
        val arrowSize = 12.dp.toPx()
        val unit = Offset(1f, 0f)
        drawZigzag(
            start = Offset(0f, y),
            end = Offset(size.width - arrowSize, y),
            color = Color.White,
            lineWidth = 2.dp.toPx(),
        )
        drawArrowHead(
            point = Offset(size.width, y),
            direction = unit,
            fill = true,
            color = Color.White,
        )
    }
}

@Composable
private fun EmotionalBondItem(
    label: String,
    drawAction: DrawScope.() -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(8.dp))
        Canvas(modifier = Modifier.width(100.dp).height(24.dp)) {
            drawAction()
        }
    }
}

private fun DrawScope.drawLineHelper(
    startX: Float,
    endX: Float,
    isDashed: Boolean = false,
    isDotted: Boolean = false,
) {
    val y = size.height / 2
    val strokeWidth = 1.5.dp.toPx()
    val pathEffect =
        when {
            isDashed -> PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            isDotted -> PathEffect.dashPathEffect(floatArrayOf(3f, 6f), 0f)
            else -> null
        }

    drawLine(
        color = Color.Gray,
        start = Offset(startX, y),
        end = Offset(endX, y),
        strokeWidth = strokeWidth,
        pathEffect = pathEffect,
    )
}

@Preview
@Composable
fun LegendsScreenPreview() {
    GenogramiaTheme {
        LegendsContent()
    }
}
