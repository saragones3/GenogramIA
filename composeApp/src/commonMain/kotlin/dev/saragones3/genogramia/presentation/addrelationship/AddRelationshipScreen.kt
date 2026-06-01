package dev.saragones3.genogramia.presentation.addrelationship

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConnectWithoutContact
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.JoinInner
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.presentation.components.DatePickerModal
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.add_relationship_bond_type
import genogramia.composeapp.generated.resources.add_relationship_child
import genogramia.composeapp.generated.resources.add_relationship_confirm
import genogramia.composeapp.generated.resources.add_relationship_consanguinity_risk
import genogramia.composeapp.generated.resources.add_relationship_effective_date
import genogramia.composeapp.generated.resources.add_relationship_emotional_bond
import genogramia.composeapp.generated.resources.add_relationship_medical_conflict
import genogramia.composeapp.generated.resources.add_relationship_parent
import genogramia.composeapp.generated.resources.add_relationship_person
import genogramia.composeapp.generated.resources.add_relationship_select_date
import genogramia.composeapp.generated.resources.add_relationship_swap_roles
import genogramia.composeapp.generated.resources.add_relationship_title
import genogramia.composeapp.generated.resources.date_format
import genogramia.composeapp.generated.resources.delete_relationship
import genogramia.composeapp.generated.resources.delete_relationship_confirmation_message
import genogramia.composeapp.generated.resources.delete_relationship_confirmation_title
import genogramia.composeapp.generated.resources.edit_relationship
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
import genogramia.composeapp.generated.resources.relationship_adoption
import genogramia.composeapp.generated.resources.relationship_cohabitation
import genogramia.composeapp.generated.resources.relationship_divorce
import genogramia.composeapp.generated.resources.relationship_fraternal_twin
import genogramia.composeapp.generated.resources.relationship_identical_twin
import genogramia.composeapp.generated.resources.relationship_marriage
import genogramia.composeapp.generated.resources.relationship_offspring
import genogramia.composeapp.generated.resources.relationship_reconciliation
import genogramia.composeapp.generated.resources.relationship_separation
import genogramia.composeapp.generated.resources.settings_cancel
import genogramia.composeapp.generated.resources.settings_confirm
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddRelationshipScreen(
    treeId: String,
    personId1: String? = null,
    personId2: String? = null,
    relationshipId: String? = null,
    onBackClick: () -> Unit,
) {
    val viewModel: AddRelationshipViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(treeId, personId1, personId2, relationshipId) {
        viewModel.onResume(treeId, personId1, personId2, relationshipId)
    }

    LaunchedEffect(state.shouldNavigateBack) {
        if (state.shouldNavigateBack) {
            onBackClick()
            viewModel.onEvent(AddRelationshipEvent.OnNavigationHandled)
        }
    }

    AddRelationshipContent(
        state = state,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddRelationshipContent(
    state: AddRelationshipState,
    onEvent: (AddRelationshipEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text =
                            stringResource(
                                if (state.relationshipId != null) {
                                    Res.string.edit_relationship
                                } else {
                                    Res.string.add_relationship_title
                                },
                            ),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                            tint = Primary,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Unspecified,
                        navigationIconContentColor = Color.Unspecified,
                        titleContentColor = Primary,
                        actionIconContentColor = Color.Unspecified,
                    ),
            )
        },
        bottomBar = {
            if (!state.isLoading) {
                Button(
                    onClick = { onEvent(AddRelationshipEvent.OnConfirmClick) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = !state.isSaving,
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = stringResource(Res.string.add_relationship_confirm).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        },
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState()),
            ) {
                RelationshipManagerCard(
                    person1 = state.person1,
                    person2 = state.person2,
                    bondType = state.bondType,
                    emotionalBond = state.emotionalBond,
                    onSwapClick = { onEvent(AddRelationshipEvent.OnSwapPersons) },
                )

                Spacer(modifier = Modifier.height(24.dp))

                BondTypeSection(
                    selectedType = state.bondType,
                    onTypeSelected = { onEvent(AddRelationshipEvent.OnBondTypeSelected(it)) },
                )

                Spacer(modifier = Modifier.height(24.dp))

                EmotionalBondSection(
                    selectedBond = state.emotionalBond,
                    onBondSelected = { onEvent(AddRelationshipEvent.OnEmotionalBondSelected(it)) },
                )

                Spacer(modifier = Modifier.height(24.dp))

                EffectiveDateSection(
                    dateFormatted = state.effectiveDateFormatted,
                    onClick = { showDatePicker = true },
                    onClearClick = {
                        onEvent(AddRelationshipEvent.OnDateSelected(null, ""))
                    },
                )

                if (state.hasConsanguinityRisk) {
                    Spacer(modifier = Modifier.height(24.dp))
                    MedicalConflictBanner()
                }

                if (state.relationshipId != null) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { showDeleteConfirmation = true },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF1F3F4),
                                contentColor = Color(0xFFB3261E),
                            ),
                        enabled = !state.isSaving,
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.delete_relationship).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showDatePicker) {
            val dateFormat = stringResource(Res.string.date_format)
            DatePickerModal(
                initialDate = state.effectiveDate,
                onDateSelected = {
                    onEvent(AddRelationshipEvent.OnDateSelected(it, dateFormat))
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false },
            )
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text(stringResource(Res.string.delete_relationship_confirmation_title)) },
                text = { Text(stringResource(Res.string.delete_relationship_confirmation_message)) },
                confirmButton = {
                    Button(
                        onClick = {
                            onEvent(AddRelationshipEvent.OnDeleteClick)
                            showDeleteConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E)),
                    ) {
                        Text(stringResource(Res.string.settings_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text(
                            text = stringResource(Res.string.settings_cancel),
                            color = Primary,
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun RelationshipManagerCard(
    person1: PersonUi?,
    person2: PersonUi?,
    bondType: Relationship.RelationshipType,
    emotionalBond: Relationship.EmotionalBond,
    onSwapClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
            ) {
                // Dotted line connecting the icons
                Canvas(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(72.dp), // Same as icon size
                ) {
                    val iconCenterY = 36.dp.toPx()
                    val swapButtonSize = 32.dp.toPx()
                    val nodeWidth = (size.width - swapButtonSize) / 2
                    val startX = nodeWidth / 2
                    val endX = size.width - (nodeWidth / 2)

                    val dashWidth = 6.dp.toPx()
                    val dashGap = 4.dp.toPx()

                    drawLine(
                        color = Color(0xFF008080),
                        start = Offset(startX, iconCenterY),
                        end = Offset(endX, iconCenterY),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, dashGap), 0f),
                    )
                }

                val role1 =
                    when (bondType) {
                        Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                        Relationship.RelationshipType.ADOPTION_LEGAL,
                        -> stringResource(Res.string.add_relationship_parent)

                        else -> stringResource(Res.string.add_relationship_person) + " 1"
                    }

                val role2 =
                    when (bondType) {
                        Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                        Relationship.RelationshipType.ADOPTION_LEGAL,
                        -> stringResource(Res.string.add_relationship_child)

                        else -> stringResource(Res.string.add_relationship_person) + " 2"
                    }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    PersonPreviewNode(
                        person = person1,
                        role = role1,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(
                        onClick = onSwapClick,
                        modifier =
                            Modifier
                                .padding(top = 84.dp)
                                .size(32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = stringResource(Res.string.add_relationship_swap_roles),
                            tint = Primary,
                        )
                    }
                    PersonPreviewNode(
                        person = person2,
                        role = role2,
                        modifier = Modifier.weight(1f),
                    )
                }

                // Bond Type Pill (above the line)
                RelationshipPill(
                    text = bondType.toText(),
                    backgroundColor = Color(0xFF1E5185),
                    modifier =
                        Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 4.dp),
                )

                // Emotional Bond Pill (below the line)
                RelationshipPill(
                    text = emotionalBond.toText(),
                    backgroundColor = Color(0xFF008080),
                    modifier =
                        Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 48.dp),
                )
            }
        }
    }
}

@Composable
private fun Relationship.RelationshipType.toText(): String =
    when (this) {
        Relationship.RelationshipType.MARRIAGE -> stringResource(Res.string.relationship_marriage)
        Relationship.RelationshipType.COHABITATION -> stringResource(Res.string.relationship_cohabitation)
        Relationship.RelationshipType.SEPARATION -> stringResource(Res.string.relationship_separation)
        Relationship.RelationshipType.DIVORCE -> stringResource(Res.string.relationship_divorce)
        Relationship.RelationshipType.RECONCILIATION -> stringResource(Res.string.relationship_reconciliation)
        Relationship.RelationshipType.BIOLOGICAL_OFFSPRING -> stringResource(Res.string.relationship_offspring)
        Relationship.RelationshipType.ADOPTION_LEGAL -> stringResource(Res.string.relationship_adoption)
        Relationship.RelationshipType.FRATERNAL_TWIN -> stringResource(Res.string.relationship_fraternal_twin)
        Relationship.RelationshipType.IDENTICAL_TWIN -> stringResource(Res.string.relationship_identical_twin)
    }

@Composable
private fun Relationship.EmotionalBond.toText(): String =
    when (this) {
        Relationship.EmotionalBond.POSITIVE -> {
            stringResource(Res.string.emotional_bond_positive)
        }

        Relationship.EmotionalBond.DISTANT -> {
            stringResource(Res.string.emotional_bond_distant)
        }

        Relationship.EmotionalBond.INTIMATE -> {
            stringResource(Res.string.emotional_bond_intimate)
        }

        Relationship.EmotionalBond.INTIMATE_CONFLICTUAL -> {
            stringResource(
                Res.string.emotional_bond_intimate_conflictual,
            )
        }

        Relationship.EmotionalBond.FOCUSED -> {
            stringResource(Res.string.emotional_bond_focused)
        }

        Relationship.EmotionalBond.FUSED -> {
            stringResource(Res.string.emotional_bond_fused)
        }

        Relationship.EmotionalBond.CONFLICTUAL -> {
            stringResource(Res.string.emotional_bond_conflictual)
        }

        Relationship.EmotionalBond.FUSED_CONFLICTUAL -> {
            stringResource(Res.string.emotional_bond_fused_conflictual)
        }

        Relationship.EmotionalBond.DIRECT_CONFLICTUAL -> {
            stringResource(Res.string.emotional_bond_direct_conflictual)
        }

        Relationship.EmotionalBond.RUPTURE -> {
            stringResource(Res.string.emotional_bond_rupture)
        }

        Relationship.EmotionalBond.ABUSE -> {
            stringResource(Res.string.emotional_bond_abuse)
        }
    }

@Composable
private fun RelationshipPill(
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier,
    ) {
        Text(
            text = text.uppercase(),
            style =
                MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                ),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun PersonPreviewNode(
    person: PersonUi?,
    role: String,
    modifier: Modifier = Modifier,
) {
    val isFemale = person?.isFemale == true
    val icon = if (isFemale) Icons.Default.Person2 else Icons.Default.Person
    val backgroundColor = if (isFemale) Color(0xFFFF8A9E) else Color(0xFF005B5C)
    val shape = if (isFemale) CircleShape else RoundedCornerShape(0.dp)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = shape,
            color = backgroundColor,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isFemale) Color(0xFF410002) else Color.White,
                    modifier = Modifier.size(40.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = person?.fullName ?: "Unknown",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        Text(
            text = role.uppercase(),
            style =
                MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 0.5.sp,
                    fontSize = 10.sp,
                ),
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BondTypeSection(
    selectedType: Relationship.RelationshipType,
    onTypeSelected: (Relationship.RelationshipType) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(Res.string.add_relationship_bond_type).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            letterSpacing = 0.5.sp,
        )
        Spacer(modifier = Modifier.height(12.dp))

        BondTypeItem(
            label = stringResource(Res.string.relationship_marriage),
            icon = Icons.Default.Favorite,
            isSelected = selectedType == Relationship.RelationshipType.MARRIAGE,
            onClick = { onTypeSelected(Relationship.RelationshipType.MARRIAGE) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.relationship_cohabitation),
            icon = Icons.Default.Home,
            isSelected = selectedType == Relationship.RelationshipType.COHABITATION,
            onClick = { onTypeSelected(Relationship.RelationshipType.COHABITATION) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.relationship_separation),
            icon = Icons.Default.ContentCut,
            isSelected = selectedType == Relationship.RelationshipType.SEPARATION,
            onClick = { onTypeSelected(Relationship.RelationshipType.SEPARATION) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.relationship_divorce),
            icon = Icons.Default.BrokenImage,
            isSelected = selectedType == Relationship.RelationshipType.DIVORCE,
            onClick = { onTypeSelected(Relationship.RelationshipType.DIVORCE) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.relationship_reconciliation),
            icon = Icons.Default.ConnectWithoutContact,
            isSelected = selectedType == Relationship.RelationshipType.RECONCILIATION,
            onClick = { onTypeSelected(Relationship.RelationshipType.RECONCILIATION) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.relationship_offspring),
            icon = Icons.Default.FamilyRestroom,
            isSelected = selectedType == Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
            onClick = { onTypeSelected(Relationship.RelationshipType.BIOLOGICAL_OFFSPRING) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.relationship_adoption),
            icon = Icons.Default.Description,
            isSelected = selectedType == Relationship.RelationshipType.ADOPTION_LEGAL,
            onClick = { onTypeSelected(Relationship.RelationshipType.ADOPTION_LEGAL) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.relationship_fraternal_twin),
            icon = Icons.Default.Groups,
            isSelected = selectedType == Relationship.RelationshipType.FRATERNAL_TWIN,
            onClick = { onTypeSelected(Relationship.RelationshipType.FRATERNAL_TWIN) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.relationship_identical_twin),
            icon = Icons.Default.Diversity3,
            isSelected = selectedType == Relationship.RelationshipType.IDENTICAL_TWIN,
            onClick = { onTypeSelected(Relationship.RelationshipType.IDENTICAL_TWIN) },
        )
    }
}

@Composable
private fun EmotionalBondSection(
    selectedBond: Relationship.EmotionalBond,
    onBondSelected: (Relationship.EmotionalBond) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(Res.string.add_relationship_emotional_bond).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            letterSpacing = 0.5.sp,
        )
        Spacer(modifier = Modifier.height(12.dp))

        BondTypeItem(
            label = stringResource(Res.string.emotional_bond_positive),
            icon = Icons.Default.SentimentSatisfied,
            isSelected = selectedBond == Relationship.EmotionalBond.POSITIVE,
            onClick = { onBondSelected(Relationship.EmotionalBond.POSITIVE) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.emotional_bond_distant),
            icon = Icons.Default.MoreHoriz,
            isSelected = selectedBond == Relationship.EmotionalBond.DISTANT,
            onClick = { onBondSelected(Relationship.EmotionalBond.DISTANT) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.emotional_bond_intimate),
            icon = Icons.Default.VolunteerActivism,
            isSelected = selectedBond == Relationship.EmotionalBond.INTIMATE,
            onClick = { onBondSelected(Relationship.EmotionalBond.INTIMATE) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.emotional_bond_intimate_conflictual),
            icon = Icons.Default.FlashOn,
            isSelected = selectedBond == Relationship.EmotionalBond.INTIMATE_CONFLICTUAL,
            onClick = { onBondSelected(Relationship.EmotionalBond.INTIMATE_CONFLICTUAL) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.emotional_bond_focused),
            icon = Icons.Default.AutoGraph,
            isSelected = selectedBond == Relationship.EmotionalBond.FOCUSED,
            onClick = { onBondSelected(Relationship.EmotionalBond.FOCUSED) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.emotional_bond_fused),
            icon = Icons.Default.JoinInner,
            isSelected = selectedBond == Relationship.EmotionalBond.FUSED,
            onClick = { onBondSelected(Relationship.EmotionalBond.FUSED) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.emotional_bond_conflictual),
            icon = Icons.Default.FlashOn,
            isSelected = selectedBond == Relationship.EmotionalBond.CONFLICTUAL,
            onClick = { onBondSelected(Relationship.EmotionalBond.CONFLICTUAL) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.emotional_bond_fused_conflictual),
            icon = Icons.Default.FlashOn,
            isSelected = selectedBond == Relationship.EmotionalBond.FUSED_CONFLICTUAL,
            onClick = { onBondSelected(Relationship.EmotionalBond.FUSED_CONFLICTUAL) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.emotional_bond_direct_conflictual),
            icon = Icons.Default.Gavel,
            isSelected = selectedBond == Relationship.EmotionalBond.DIRECT_CONFLICTUAL,
            onClick = { onBondSelected(Relationship.EmotionalBond.DIRECT_CONFLICTUAL) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.emotional_bond_rupture),
            icon = Icons.Default.LinkOff,
            isSelected = selectedBond == Relationship.EmotionalBond.RUPTURE,
            onClick = { onBondSelected(Relationship.EmotionalBond.RUPTURE) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        BondTypeItem(
            label = stringResource(Res.string.emotional_bond_abuse),
            icon = Icons.Default.Warning,
            isSelected = selectedBond == Relationship.EmotionalBond.ABUSE,
            onClick = { onBondSelected(Relationship.EmotionalBond.ABUSE) },
        )
    }
}

@Composable
private fun BondTypeItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Primary else Color.White,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        border = if (!isSelected) borderStroke() else null,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Primary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) Color.White else Color.Black,
                modifier = Modifier.weight(1f),
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun EffectiveDateSection(
    dateFormatted: String?,
    onClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(Res.string.add_relationship_effective_date).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            letterSpacing = 0.5.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF1F3F4),
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clickable { onClick() },
                )
                Row(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text =
                            if (dateFormatted.isNullOrBlank()) {
                                stringResource(
                                    Res.string.add_relationship_select_date,
                                )
                            } else {
                                dateFormatted
                            },
                        color = if (!dateFormatted.isNullOrBlank()) Color.Black else Color.Gray,
                        modifier = Modifier.weight(1f),
                    )
                    if (dateFormatted.isNullOrBlank()) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp),
                        )
                    } else {
                        IconButton(
                            onClick = onClearClick,
                            modifier = Modifier.size(24.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicalConflictBanner() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(Res.string.add_relationship_medical_conflict).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            letterSpacing = 0.5.sp,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            color = Color(0xFFFFDADA),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.add_relationship_consanguinity_risk),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF410002),
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = Color(0xFF410002),
                )
            }
        }
    }
}

@Composable
private fun borderStroke() = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))

private class AddRelationshipStateProvider : PreviewParameterProvider<AddRelationshipState> {
    private val seed =
        AddRelationshipState(
            person1 =
                PersonUi(
                    id = "1",
                    fullName = "Johnathan Doe",
                    isFemale = false,
                ),
            person2 =
                PersonUi(
                    id = "2",
                    fullName = "Elena Rossi",
                    isFemale = true,
                ),
        )

    override val values =
        sequenceOf(
            seed,
            seed.copy(
                person1 = seed.person1?.copy(isFemale = false),
                person2 = seed.person2?.copy(isFemale = false),
                bondType = Relationship.RelationshipType.COHABITATION,
                emotionalBond = Relationship.EmotionalBond.INTIMATE,
            ),
            seed.copy(
                person1 = seed.person1?.copy(isFemale = true),
                person2 = seed.person2?.copy(isFemale = true),
                bondType = Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                emotionalBond = Relationship.EmotionalBond.DIRECT_CONFLICTUAL,
            ),
            seed.copy(
                person1 = seed.person1?.copy(fullName = "Johnathan Doe with a large lastname"),
                person2 = seed.person2?.copy(fullName = "Elena Rossi with a large lastname"),
                bondType = Relationship.RelationshipType.DIVORCE,
                emotionalBond = Relationship.EmotionalBond.ABUSE,
                hasConsanguinityRisk = true,
            ),
        )
}

@Preview
@Composable
private fun AddRelationshipScreenPreview(
    @PreviewParameter(AddRelationshipStateProvider::class) state: AddRelationshipState,
) {
    GenogramiaTheme {
        AddRelationshipContent(
            state = state,
            onEvent = {},
            onBackClick = {},
        )
    }
}
