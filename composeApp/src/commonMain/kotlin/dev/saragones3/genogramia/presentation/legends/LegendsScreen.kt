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
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.presentation.util.drawDeathMark
import dev.saragones3.genogramia.presentation.util.drawEmotionalBondLine
import dev.saragones3.genogramia.presentation.util.drawSexualOrientationMark
import dev.saragones3.genogramia.presentation.util.drawStructuralRelationshipLine
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
    ) { p1, p2, nodeSize ->
        drawStructuralRelationshipLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            type = Relationship.RelationshipType.MARRIAGE,
            color = Color.Gray,
        )
    }
}

@Composable
private fun CohabitationItem() {
    RelationshipItem(
        title = stringResource(Res.string.relationship_cohabitation),
        subtitle = stringResource(Res.string.legends_rel_cohabitation_desc),
    ) { p1, p2, nodeSize ->
        drawStructuralRelationshipLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            type = Relationship.RelationshipType.COHABITATION,
            color = Color.Gray,
        )
    }
}

@Composable
private fun SeparationItem() {
    RelationshipItem(
        title = stringResource(Res.string.relationship_separation),
        subtitle = stringResource(Res.string.legends_rel_separation_desc),
    ) { p1, p2, nodeSize ->
        drawStructuralRelationshipLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            type = Relationship.RelationshipType.SEPARATION,
            color = Color.Gray,
        )
    }
}

@Composable
private fun DivorceItem() {
    RelationshipItem(
        title = stringResource(Res.string.relationship_divorce),
        subtitle = stringResource(Res.string.legends_rel_divorce_desc),
    ) { p1, p2, nodeSize ->
        drawStructuralRelationshipLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            type = Relationship.RelationshipType.DIVORCE,
            color = Color.Gray,
        )
    }
}

@Composable
private fun ReconciliationItem() {
    RelationshipItem(
        title = stringResource(Res.string.relationship_reconciliation),
        subtitle = stringResource(Res.string.legends_rel_reconciliation_desc),
    ) { p1, p2, nodeSize ->
        drawStructuralRelationshipLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            type = Relationship.RelationshipType.RECONCILIATION,
            color = Color.Gray,
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
    drawAction: DrawScope.(Offset, Offset, Float) -> Unit,
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
            Canvas(modifier = Modifier.width(100.dp).height(64.dp)) {
                val nodeSize = 16.dp.toPx()
                val strokeWidth = 1.5.dp.toPx()
                // Shift everything up a bit to make room for the bracket
                val centerY = size.height / 2 - 8.dp.toPx()
                val squareCenter = Offset(nodeSize / 2, centerY)
                val circleCenter = Offset(size.width - nodeSize / 2, centerY)

                drawRect(
                    color = Color(0xFF136299),
                    topLeft = Offset(squareCenter.x - nodeSize / 2, squareCenter.y - nodeSize / 2),
                    size = Size(nodeSize, nodeSize),
                    style = Stroke(width = strokeWidth),
                )
                drawCircle(
                    color = Color(0xFF884364),
                    center = circleCenter,
                    radius = nodeSize / 2,
                    style = Stroke(width = strokeWidth),
                )
                drawAction(squareCenter, circleCenter, nodeSize)
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
    EmotionalBondItem(stringResource(Res.string.emotional_bond_positive)) { p1, p2, nodeSize ->
        drawEmotionalBondLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            bond = Relationship.EmotionalBond.POSITIVE,
            color = Color.White,
        )
    }
}

@Composable
private fun DistantItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_distant)) { p1, p2, nodeSize ->
        drawEmotionalBondLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            bond = Relationship.EmotionalBond.DISTANT,
            color = Color.White,
        )
    }
}

@Composable
private fun IntimateItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_intimate)) { p1, p2, nodeSize ->
        drawEmotionalBondLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            bond = Relationship.EmotionalBond.INTIMATE,
            color = Color.White,
        )
    }
}

@Composable
private fun IntimateConflictualItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_intimate_conflictual)) { p1, p2, nodeSize ->
        drawEmotionalBondLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            bond = Relationship.EmotionalBond.INTIMATE_CONFLICTUAL,
            color = Color.White,
        )
    }
}

@Composable
private fun FocusedItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_focused)) { p1, p2, nodeSize ->
        drawEmotionalBondLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            bond = Relationship.EmotionalBond.FOCUSED,
            color = Color.White,
        )
    }
}

@Composable
private fun FusedItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_fused)) { p1, p2, nodeSize ->
        drawEmotionalBondLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            bond = Relationship.EmotionalBond.FUSED,
            color = Color.White,
        )
    }
}

@Composable
private fun ConflictualItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_conflictual)) { p1, p2, nodeSize ->
        drawEmotionalBondLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            bond = Relationship.EmotionalBond.CONFLICTUAL,
            color = Color.White,
        )
    }
}

@Composable
private fun FusedConflictualItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_fused_conflictual)) { p1, p2, nodeSize ->
        drawEmotionalBondLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            bond = Relationship.EmotionalBond.FUSED_CONFLICTUAL,
            color = Color.White,
        )
    }
}

@Composable
private fun DirectConflictualItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_direct_conflictual)) { p1, p2, nodeSize ->
        drawEmotionalBondLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            bond = Relationship.EmotionalBond.DIRECT_CONFLICTUAL,
            color = Color.White,
        )
    }
}

@Composable
private fun RuptureItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_rupture)) { p1, p2, nodeSize ->
        drawEmotionalBondLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            bond = Relationship.EmotionalBond.RUPTURE,
            color = Color.White,
        )
    }
}

@Composable
private fun AbuseItem() {
    EmotionalBondItem(stringResource(Res.string.emotional_bond_abuse)) { p1, p2, nodeSize ->
        drawEmotionalBondLine(
            p1Center = p1,
            p2Center = p2,
            nodeSize = nodeSize,
            bond = Relationship.EmotionalBond.ABUSE,
            color = Color.White,
        )
    }
}

@Composable
private fun EmotionalBondItem(
    label: String,
    drawAction: DrawScope.(Offset, Offset, Float) -> Unit,
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
        Canvas(modifier = Modifier.width(100.dp).height(48.dp)) {
            val nodeSize = 24.dp.toPx()
            val strokeWidth = 1.5.dp.toPx()
            val squareCenter = Offset(nodeSize / 2, size.height / 2)
            val circleCenter = Offset(size.width - nodeSize / 2, size.height / 2)

            drawRect(
                color = Color.White,
                topLeft = Offset(squareCenter.x - nodeSize / 2, squareCenter.y - nodeSize / 2),
                size = Size(nodeSize, nodeSize),
                style = Stroke(width = strokeWidth),
            )
            drawCircle(
                color = Color.White,
                center = circleCenter,
                radius = nodeSize / 2,
                style = Stroke(width = strokeWidth),
            )
            drawAction(squareCenter, circleCenter, nodeSize)
        }
    }
}

@Preview
@Composable
fun LegendsScreenPreview() {
    GenogramiaTheme {
        LegendsContent()
    }
}
