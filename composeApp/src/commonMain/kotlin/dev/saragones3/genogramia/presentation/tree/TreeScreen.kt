package dev.saragones3.genogramia.presentation.tree

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.add_relationship
import genogramia.composeapp.generated.resources.canvas_reset
import genogramia.composeapp.generated.resources.canvas_zoom_in
import genogramia.composeapp.generated.resources.canvas_zoom_out
import genogramia.composeapp.generated.resources.edit_relationship
import genogramia.composeapp.generated.resources.error_tree_not_found
import genogramia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TreeScreen(
    treeId: String,
    onBackClick: () -> Unit,
    onAddPersonClick: (String, String?) -> Unit,
    onAddRelationshipClick: (String, String, String) -> Unit,
    onEditRelationshipClick: (String, String, String, String) -> Unit,
) {
    val viewModel: TreeViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val treeNotFoundErr = stringResource(Res.string.error_tree_not_found)
    val unknownErr = stringResource(Res.string.error_unknown)

    LaunchedEffect(treeId) {
        viewModel.onEvent(TreeEvent.LoadTree(treeId))
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            val message =
                when (error) {
                    TreeError.NOT_FOUND -> treeNotFoundErr
                    TreeError.UNKNOWN -> unknownErr
                }
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(TreeEvent.OnErrorConsumed)
        }
    }

    LaunchedEffect(state.shouldNavigateBack) {
        if (state.shouldNavigateBack) {
            onBackClick()
            viewModel.onEvent(TreeEvent.OnNavigationConsumed)
        }
    }

    TreeContent(
        state = state,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick,
        onAddPersonClick = { onAddPersonClick(treeId, null) },
        onEditPersonClick = { personId -> onAddPersonClick(treeId, personId) },
        onAddRelationshipClick = { p1, p2 -> onAddRelationshipClick(treeId, p1, p2) },
        onEditRelationshipClick = { relId, p1Id, p2Id ->
            onEditRelationshipClick(treeId, relId, p1Id, p2Id)
        },
        snackbarHostState = snackbarHostState,
    )
}

@Composable
private fun TreeContent(
    state: TreeState,
    onEvent: (TreeEvent) -> Unit,
    onBackClick: () -> Unit,
    onAddPersonClick: () -> Unit,
    onEditPersonClick: (String) -> Unit,
    onAddRelationshipClick: (String, String) -> Unit,
    onEditRelationshipClick: (String, String, String) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopBar(
                treeName = state.tree.name,
                onBackClick = onBackClick,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (state.selectedPersonIds.size < 2) {
                FloatingActionButton(
                    onClick = {
                        if (state.selectedPersonIds.size == 1) {
                            onEditPersonClick(state.selectedPersonIds.first())
                        } else {
                            onAddPersonClick()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp),
                ) {
                    Icon(
                        imageVector = if (state.selectedPersonIds.size == 1) Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = null,
                    )
                }
            }
        },
    ) { padding ->
        BoxWithConstraints(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),
        ) {
            val center = Offset(constraints.maxWidth / 2f, constraints.maxHeight / 2f)
            val density = LocalDensity.current

            val resetViewport = {
                val centralPerson = state.tree.centralPerson
                val personPosPx =
                    Offset(
                        centralPerson.position.x * density.density,
                        centralPerson.position.y * density.density,
                    )
                onEvent(TreeEvent.OnResetToCenter(center - personPosPx))
            }

            // Initial centering when tree is loaded
            var lastLoadedTreeId by remember { mutableStateOf("") }
            LaunchedEffect(state.tree.id) {
                if (state.tree.id.isNotEmpty() && state.tree.id != lastLoadedTreeId) {
                    resetViewport()
                    lastLoadedTreeId = state.tree.id
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            GenogramCanvas(
                state = state,
                onEvent = onEvent,
                onAddRelationshipClick = onAddRelationshipClick,
                onEditRelationshipClick = onEditRelationshipClick,
            )

            CanvasControls(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                onZoomIn = { onEvent(TreeEvent.OnZoomIn()) },
                onZoomOut = { onEvent(TreeEvent.OnZoomOut()) },
                onReset = resetViewport,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    treeName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = treeName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
    )
}

@Composable
private fun GenogramCanvas(
    state: TreeState,
    onEvent: (TreeEvent) -> Unit,
    onAddRelationshipClick: (String, String) -> Unit,
    onEditRelationshipClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val persons = remember(state.tree) { listOf(state.tree.centralPerson) + state.tree.persons }
    val offset = state.offset
    val scale = state.scale
    val selectedPersonIds = state.selectedPersonIds

    val density = LocalDensity.current
    val nodeSize = with(density) { NODE_SIZE.toPx() }
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val tappedPerson =
                            persons.find {
                                val centerX = it.position.x * density.density
                                val centerY = it.position.y * density.density
                                val personTopLeft =
                                    Offset(centerX - nodeSize / 2, centerY - nodeSize / 2)
                                tapOffset.x >= (personTopLeft.x * scale + offset.x) &&
                                    tapOffset.x <= ((personTopLeft.x + nodeSize) * scale + offset.x) &&
                                    tapOffset.y >= (personTopLeft.y * scale + offset.y) &&
                                    tapOffset.y <= ((personTopLeft.y + nodeSize) * scale + offset.y)
                            }

                        if (tappedPerson != null) {
                            onEvent(TreeEvent.OnPersonSelected(tappedPerson.id))
                        } else {
                            onEvent(TreeEvent.OnDismissSelection)
                        }
                    }
                }.pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        onEvent(TreeEvent.OnTransform(centroid, pan, zoom))
                    }
                },
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = offset.x
                        translationY = offset.y
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin(0f, 0f)
                    },
        ) {
            GridBackground()

            Canvas(modifier = Modifier.fillMaxSize()) {
                val horizontalRelationships = state.tree.relationships.filter { it.type.isStructural }
                drawHorizontalRelationship(
                    relationships = horizontalRelationships,
                    persons = persons,
                    density = density.density,
                    nodeSize = nodeSize,
                )
                drawVerticalRelationship(
                    relationships = state.tree.relationships,
                    horizontalRelationships = horizontalRelationships,
                    persons = persons,
                    density = density.density,
                    nodeSize = nodeSize,
                )
                drawNewRelationshipLine(
                    selectedPersonIds = selectedPersonIds,
                    persons = persons,
                    density = density.density,
                )
            }
            persons.forEach { person ->
                key(person.id) {
                    PersonNodeView(
                        person = person,
                        isSelected = selectedPersonIds.contains(person.id),
                        onSelected = { onEvent(TreeEvent.OnPersonSelected(person.id)) },
                        onMove = { delta -> onEvent(TreeEvent.OnPersonMove(person.id, delta)) },
                        onMoveFinished = { onEvent(TreeEvent.OnPersonMoveFinished(person.id)) },
                    )
                }
            }

            if (selectedPersonIds.size == 2) {
                val p1 = persons.find { it.id == selectedPersonIds.first() }
                val p2 = persons.find { it.id == selectedPersonIds.last() }
                RelationshipTooltip(
                    p1 = p1,
                    p2 = p2,
                    isEdit = state.selectedRelationshipId != null,
                    onClick = {
                        if (p1 != null && p2 != null) {
                            if (state.selectedRelationshipId != null) {
                                onEditRelationshipClick(state.selectedRelationshipId, p1.id, p2.id)
                            } else {
                                onAddRelationshipClick(p1.id, p2.id)
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun GridBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val gridStep = 40.dp.toPx()
        val color = Color.LightGray.copy(alpha = 0.3f)
        val gridCount = 200
        val size = gridStep * gridCount
        val start = -size / 2

        for (i in 0..gridCount) {
            val pos = start + i * gridStep
            drawLine(color, Offset(pos, start), Offset(pos, start + size), 1f)
            drawLine(color, Offset(start, pos), Offset(start + size, pos), 1f)
        }
    }
}

private fun DrawScope.drawNewRelationshipLine(
    selectedPersonIds: List<String>,
    persons: List<PersonNodeUi>,
    density: Float,
) {
    if (selectedPersonIds.size == 2) {
        val p1 = persons.find { it.id == selectedPersonIds.first() }
        val p2 = persons.find { it.id == selectedPersonIds.last() }
        if (p1 != null && p2 != null) {
            drawLine(
                color = Color(0xFF008080),
                start = p1.position * density,
                end = p2.position * density,
                strokeWidth = 2.dp.toPx(),
                pathEffect =
                    PathEffect.dashPathEffect(
                        floatArrayOf(10f, 10f),
                        0f,
                    ),
            )
        }
    }
}

private fun DrawScope.drawHorizontalRelationship(
    relationships: List<RelationshipUi>,
    persons: List<PersonNodeUi>,
    density: Float,
    nodeSize: Float,
) {
    relationships.forEach { relationship ->
        val p1 = persons.find { it.id == relationship.personId1 }
        val p2 = persons.find { it.id == relationship.personId2 }
        if (p1 != null && p2 != null) {
            val p1Center = p1.position * density
            val p2Center = p2.position * density

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

            drawLine(
                color = LINE_COLOR,
                start = start,
                end = end,
                strokeWidth = LINE_WIDTH.toPx(),
                pathEffect =
                    PathEffect
                        .dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        .takeIf { relationship.type == Relationship.RelationshipType.COHABITATION },
            )
        }
    }
}

private fun DrawScope.drawVerticalRelationship(
    relationships: List<RelationshipUi>,
    horizontalRelationships: List<RelationshipUi>,
    persons: List<PersonNodeUi>,
    density: Float,
    nodeSize: Float,
) {
    val groupedByChild =
        relationships
            .filter { it.type == Relationship.RelationshipType.BIOLOGICAL_OFFSPRING }
            .groupBy { it.personId2 }
    groupedByChild.forEach { (childId, parents) ->
        val child = persons.find { it.id == childId } ?: return@forEach
        val childTop = child.position * density - Offset(0f, nodeSize / 2)

        if (parents.size == 2) {
            val p1Id = parents.first().personId1
            val p2Id = parents.last().personId1
            val marriage =
                horizontalRelationships.find {
                    (it.personId1 == p1Id && it.personId2 == p2Id) ||
                        (it.personId1 == p2Id && it.personId2 == p1Id)
                }

            if (marriage != null) {
                val parent1 = persons.find { it.id == p1Id }!!
                val parent2 = persons.find { it.id == p2Id }!!

                val p1Center = parent1.position * density
                val p2Center = parent2.position * density

                val midpointX = (p1Center.x + p2Center.x) / 2
                val marriageY =
                    if (p1Center.y != p2Center.y) {
                        (p1Center.y + p2Center.y) / 2
                    } else {
                        p1Center.y
                    }

                val path =
                    Path().apply {
                        moveTo(midpointX, marriageY)
                        val midY = childTop.y - 50f
                        lineTo(midpointX, midY)
                        lineTo(childTop.x, midY)
                        lineTo(childTop.x, childTop.y)
                    }
                drawPath(
                    path = path,
                    color = LINE_COLOR,
                    style = Stroke(width = LINE_WIDTH.toPx()),
                )
            }
        } else if (parents.size == 1) {
            val parent = persons.find { it.id == parents.first().personId1 }!!
            val pCenter = parent.position * density
            val pBottomY = pCenter.y + nodeSize / 2
            val path =
                Path().apply {
                    moveTo(pCenter.x, pBottomY)
                    lineTo(childTop.x, childTop.y)
                }
            drawPath(
                path = path,
                color = LINE_COLOR,
                style = Stroke(width = LINE_WIDTH.toPx()),
            )
        }
    }
}

@Composable
private fun PersonNodeView(
    person: PersonNodeUi,
    isSelected: Boolean,
    onSelected: () -> Unit,
    onMove: (Offset) -> Unit,
    onMoveFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val currentOnMove by rememberUpdatedState(onMove)
    val currentOnMoveFinished by rememberUpdatedState(onMoveFinished)
    val shape =
        if (person.biologicalSex == Person.BiologicalSex.FEMALE) {
            CircleShape
        } else {
            RoundedCornerShape(12.dp)
        }

    val backgroundColor =
        when (person.biologicalSex) {
            Person.BiologicalSex.MALE -> Color(0xFFD1E4FF)
            Person.BiologicalSex.FEMALE -> Color(0xFFFFD1DC)
            else -> MaterialTheme.colorScheme.secondaryContainer
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .offset {
                    IntOffset(
                        (person.position.x.dp - 50.dp).roundToPx(),
                        (person.position.y.dp - 56.dp).roundToPx(),
                    )
                }.width(100.dp)
                .combinedClickable(
                    onClick = onSelected,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ).pointerInput(person.id) {
                    detectDragGestures(
                        onDragEnd = { currentOnMoveFinished() },
                        onDragCancel = { currentOnMoveFinished() },
                    ) { change, dragAmount ->
                        change.consume()
                        val deltaDp =
                            with(density) {
                                Offset(
                                    x = dragAmount.x.toDp().value,
                                    y = dragAmount.y.toDp().value,
                                )
                            }
                        currentOnMove(deltaDp)
                    }
                },
    ) {
        PersonDates(
            birthDate = person.birthDateText,
            deathDate = person.deathDateText,
        )

        Spacer(modifier = Modifier.height(4.dp))

        PersonShape(
            person = person,
            shape = shape,
            backgroundColor = backgroundColor,
            size = with(density) { NODE_SIZE.toPx() },
            modifier =
                Modifier
                    .nodeBorder(shape, isSelected, person.isIndexPerson)
                    .background(backgroundColor, shape)
                    .size(NODE_SIZE)
                    .padding(4.dp),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${person.firstName} ${person.lastName}",
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                ),
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

@Composable
private fun PersonDates(
    birthDate: String,
    deathDate: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (birthDate.isNotEmpty()) {
            Text(
                text = birthDate,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            )
        }
        if (birthDate.isNotEmpty() && deathDate.isNotEmpty()) {
            Text(text = "-", modifier = Modifier)
        }
        if (deathDate.isNotEmpty()) {
            Text(
                text = deathDate,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            )
        }
    }
}

@Composable
private fun PersonShape(
    person: PersonNodeUi,
    shape: Shape,
    backgroundColor: Color,
    size: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (person.isDeceased) {
            Canvas(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(if (shape == CircleShape) 6.dp else 0.dp),
            ) {
                drawDeathMark()
            }
        }
        if (person.sexualOrientation == Person.SexualOrientation.OTHER) {
            Canvas(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(if (shape == CircleShape) 6.dp else 0.dp),
            ) {
                drawSexualOrientationMark(nodeSize = size)
            }
        }

        if (person.age.isNotEmpty()) {
            Text(
                text = person.age,
                style =
                    MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                textAlign = TextAlign.Center,
                modifier = Modifier.background(backgroundColor),
            )
        }
    }
}

private fun DrawScope.drawDeathMark() {
    drawLine(
        color = Color.Black,
        start = Offset.Zero,
        end = Offset(size.width, size.height),
        strokeWidth = 4f,
    )
    drawLine(
        color = Color.Black,
        start = Offset(size.width, 0f),
        end = Offset(0f, size.height),
        strokeWidth = 4f,
    )
}

private fun DrawScope.drawSexualOrientationMark(nodeSize: Float) {
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
        color = Color.Black,
        style = Stroke(width = 2f),
    )
}

@Composable
private fun RelationshipTooltip(
    p1: PersonNodeUi?,
    p2: PersonNodeUi?,
    isEdit: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (p1 != null && p2 != null) {
        val tooltipCenter = (p1.position + p2.position) / 2f

        Surface(
            onClick = onClick,
            modifier =
                modifier
                    .offset(tooltipCenter.x.dp, tooltipCenter.y.dp)
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        layout(placeable.width, placeable.height) {
                            placeable.placeRelative(-placeable.width / 2, -placeable.height / 2)
                        }
                    }.pointerInput(Unit) {
                        // Prevent taps on tooltip from being handled by the canvas
                        detectTapGestures { }
                    }.height(32.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 4.dp,
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text =
                        stringResource(
                            if (isEdit) Res.string.edit_relationship else Res.string.add_relationship,
                        ),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    softWrap = false,
                )
            }
        }
    }
}

@Composable
private fun CanvasControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(56.dp).width(180.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            IconButton(onClick = onZoomOut) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = stringResource(Res.string.canvas_zoom_out),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            VerticalDivider(
                modifier = Modifier.fillMaxHeight().padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            )

            IconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Default.FilterCenterFocus,
                    contentDescription = stringResource(Res.string.canvas_reset),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            VerticalDivider(
                modifier = Modifier.fillMaxHeight().padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            )

            IconButton(onClick = onZoomIn) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.canvas_zoom_in),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

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

private val NODE_SIZE = 64.dp
private val LINE_COLOR = Color(0xFFBDBDBD)
private val LINE_WIDTH = 1.5.dp

private class TreeStateProvider : PreviewParameterProvider<TreeState> {
    private val seed =
        TreeUi(
            id = "tree-123",
            name = "Ancestral record",
            centralPerson =
                PersonNodeUi(
                    id = "1",
                    firstName = "María Elena",
                    lastName = "García López",
                    biologicalSex = Person.BiologicalSex.FEMALE,
                    sexualOrientation = Person.SexualOrientation.HETEROSEXUAL,
                    birthDateText = "1980",
                    age = "44",
                    isIndexPerson = true,
                    position = Offset(100f, 100f),
                ),
            persons =
                listOf(
                    PersonNodeUi(
                        id = "2",
                        firstName = "Ángel",
                        lastName = "González Martínez",
                        biologicalSex = Person.BiologicalSex.MALE,
                        birthDateText = "1915",
                        deathDateText = "1989",
                        age = "74",
                        isDeceased = true,
                        position = Offset(300f, 100f),
                    ),
                    PersonNodeUi(
                        id = "3",
                        firstName = "Sara",
                        lastName = "García González",
                        biologicalSex = Person.BiologicalSex.FEMALE,
                        sexualOrientation = Person.SexualOrientation.OTHER,
                        birthDateText = "1990",
                        deathDateText = "2021",
                        age = "31",
                        isDeceased = true,
                        position = Offset(125f, 300f),
                    ),
                    PersonNodeUi(
                        id = "4",
                        firstName = "Juan",
                        lastName = "García Pérez",
                        biologicalSex = Person.BiologicalSex.MALE,
                        sexualOrientation = Person.SexualOrientation.OTHER,
                        birthDateText = "2001",
                        age = "25",
                        position = Offset(75f, 500f),
                    ),
                    PersonNodeUi(
                        id = "5",
                        firstName = "Juan",
                        lastName = "García González",
                        biologicalSex = Person.BiologicalSex.MALE,
                        sexualOrientation = Person.SexualOrientation.HETEROSEXUAL,
                        birthDateText = "1990",
                        age = "36",
                        position = Offset(275f, 300f),
                    ),
                    PersonNodeUi(
                        id = "6",
                        firstName = "Rodrigo",
                        lastName = "García González",
                        biologicalSex = Person.BiologicalSex.MALE,
                        sexualOrientation = Person.SexualOrientation.HETEROSEXUAL,
                        birthDateText = "2019",
                        age = "7",
                        position = Offset(275f, 500f),
                    ),
                ),
        )

    override val values =
        sequenceOf(
            TreeState(
                tree =
                    seed.copy(
                        relationships =
                            listOf(
                                RelationshipUi(
                                    id = "rel-1-2",
                                    personId1 = "1",
                                    personId2 = "2",
                                    type = Relationship.RelationshipType.MARRIAGE,
                                    emotionalBond = Relationship.EmotionalBond.POSITIVE,
                                ),
                                RelationshipUi(
                                    id = "rel-1-3",
                                    personId1 = "1",
                                    personId2 = "3",
                                    type = Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                                    emotionalBond = Relationship.EmotionalBond.POSITIVE,
                                ),
                                RelationshipUi(
                                    id = "rel-2-3",
                                    personId1 = "2",
                                    personId2 = "3",
                                    type = Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                                    emotionalBond = Relationship.EmotionalBond.POSITIVE,
                                ),
                                RelationshipUi(
                                    id = "rel-1-5",
                                    personId1 = "1",
                                    personId2 = "5",
                                    type = Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                                    emotionalBond = Relationship.EmotionalBond.POSITIVE,
                                ),
                                RelationshipUi(
                                    id = "rel-2-5",
                                    personId1 = "2",
                                    personId2 = "5",
                                    type = Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                                    emotionalBond = Relationship.EmotionalBond.POSITIVE,
                                ),
                                RelationshipUi(
                                    id = "rel-3-4",
                                    personId1 = "3",
                                    personId2 = "4",
                                    type = Relationship.RelationshipType.COHABITATION,
                                    emotionalBond = Relationship.EmotionalBond.POSITIVE,
                                ),
                                RelationshipUi(
                                    id = "rel-5-6",
                                    personId1 = "5",
                                    personId2 = "6",
                                    type = Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                                    emotionalBond = Relationship.EmotionalBond.POSITIVE,
                                ),
                            ),
                    ),
            ),
            TreeState(
                tree =
                    seed.copy(
                        relationships =
                            listOf(
                                RelationshipUi(
                                    id = "rel-1-2",
                                    personId1 = "1",
                                    personId2 = "2",
                                    type = Relationship.RelationshipType.COHABITATION,
                                    emotionalBond = Relationship.EmotionalBond.CONFLICTUAL,
                                ),
                            ),
                    ),
                selectedPersonIds = listOf("3", "4"),
            ),
            TreeState(
                tree =
                    seed.copy(
                        relationships =
                            listOf(
                                RelationshipUi(
                                    id = "rel-1-2",
                                    personId1 = "1",
                                    personId2 = "2",
                                    type = Relationship.RelationshipType.COHABITATION,
                                    emotionalBond = Relationship.EmotionalBond.CONFLICTUAL,
                                ),
                            ),
                    ),
                offset = Offset(-600f, -300f),
                scale = 3f,
            ),
            TreeState(isLoading = true),
        )
}

@Composable
@Preview
private fun TreeScreenPreview(
    @PreviewParameter(TreeStateProvider::class) state: TreeState,
) {
    GenogramiaTheme {
        TreeContent(
            state = state,
            onEvent = {},
            onBackClick = {},
            onAddPersonClick = {},
            onEditPersonClick = {},
            onAddRelationshipClick = { _, _ -> },
            onEditRelationshipClick = { _, _, _ -> },
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
