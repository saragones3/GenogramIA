package dev.saragones3.genogramia.presentation.tree

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ColorScheme
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
import androidx.compose.material3.Typography
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.add_relationship
import genogramia.composeapp.generated.resources.canvas_reset
import genogramia.composeapp.generated.resources.canvas_zoom_in
import genogramia.composeapp.generated.resources.canvas_zoom_out
import genogramia.composeapp.generated.resources.error_tree_not_found
import genogramia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TreeScreen(
    treeId: String,
    onBackClick: () -> Unit,
    onAddPersonClick: (String, String?) -> Unit,
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
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TreeContent(
    state: TreeState,
    onEvent: (TreeEvent) -> Unit,
    onBackClick: () -> Unit,
    onAddPersonClick: () -> Unit,
    onEditPersonClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.tree.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
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

            // Initial centering when tree is loaded
            var initialized by remember { mutableStateOf(false) }
            LaunchedEffect(state.tree) {
                if (!initialized) {
                    onEvent(TreeEvent.OnResetToCenter(center))
                    initialized = true
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            GenogramCanvas(
                tree = state.tree,
                offset = state.offset,
                scale = state.scale,
                selectedPersonIds = state.selectedPersonIds,
                onTransform = { pan, zoom ->
                    onEvent(TreeEvent.OnTransform(pan, zoom))
                },
                onPersonSelected = { onEvent(TreeEvent.OnPersonSelected(it)) },
                onDismissSelection = { onEvent(TreeEvent.OnDismissSelection) },
                onAddRelationshipClick = {
                    onEvent(TreeEvent.OnAddRelationship)
                },
            )

            CanvasControls(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                onZoomIn = { onEvent(TreeEvent.OnZoomIn()) },
                onZoomOut = { onEvent(TreeEvent.OnZoomOut()) },
                onReset = { onEvent(TreeEvent.OnResetToCenter(center)) },
            )
        }
    }
}

@Composable
private fun GenogramCanvas(
    tree: TreeUi,
    offset: Offset,
    scale: Float,
    selectedPersonIds: List<String>,
    onTransform: (Offset, Float) -> Unit,
    onPersonSelected: (String) -> Unit,
    onDismissSelection: () -> Unit,
    onAddRelationshipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val theme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val nodeSize = with(LocalDensity.current) { 64.dp.toPx() }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val tappedPerson =
                            (listOf(tree.centralPerson) + tree.persons).find {
                                val personTopLeft =
                                    Offset(it.position.x - nodeSize / 2, it.position.y - nodeSize / 2)
                                tapOffset.x >= (personTopLeft.x * scale + offset.x) &&
                                    tapOffset.x <= ((personTopLeft.x + nodeSize) * scale + offset.x) &&
                                    tapOffset.y >= (personTopLeft.y * scale + offset.y) &&
                                    tapOffset.y <= ((personTopLeft.y + nodeSize) * scale + offset.y)
                            }

                        if (tappedPerson != null) {
                            onPersonSelected(tappedPerson.id)
                        } else {
                            onDismissSelection()
                        }
                    }
                }.pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        onTransform(pan, zoom)
                    }
                },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            withTransform(
                {
                    translate(offset.x, offset.y)
                    scale(scale, scale)
                },
            ) {
                // Draw Infinite Grid
                val gridStep = 40.dp.toPx()
                val color = Color.LightGray.copy(alpha = 0.3f)

                val visibleWidth = size.width / scale
                val visibleHeight = size.height / scale

                val startX = -offset.x / scale
                val startY = -offset.y / scale

                val left = ((startX / gridStep).toInt() * gridStep) - gridStep
                val top = ((startY / gridStep).toInt() * gridStep) - gridStep
                val right = left + visibleWidth + (gridStep * 2)
                val bottom = top + visibleHeight + (gridStep * 2)

                var x = left
                while (x < right) {
                    drawLine(
                        color = color,
                        start = Offset(x, top),
                        end = Offset(x, bottom),
                        strokeWidth = 1f,
                    )
                    x += gridStep
                }

                var y = top
                while (y < bottom) {
                    drawLine(
                        color = color,
                        start = Offset(left, y),
                        end = Offset(right, y),
                        strokeWidth = 1f,
                    )
                    y += gridStep
                }

                // Draw persons
                drawPerson(
                    person = tree.centralPerson,
                    textMeasurer = textMeasurer,
                    theme = theme,
                    typography = typography,
                    isSelected = selectedPersonIds.contains(tree.centralPerson.id),
                )
                tree.persons.forEach { person ->
                    drawPerson(
                        person = person,
                        textMeasurer = textMeasurer,
                        theme = theme,
                        typography = typography,
                        isSelected = selectedPersonIds.contains(person.id),
                    )
                }

                // Draw dashed line between selected persons
                if (selectedPersonIds.size == 2) {
                    val p1 = (listOf(tree.centralPerson) + tree.persons).find { it.id == selectedPersonIds.first() }
                    val p2 = (listOf(tree.centralPerson) + tree.persons).find { it.id == selectedPersonIds.last() }
                    if (p1 != null && p2 != null) {
                        drawLine(
                            color = Color(0xFF008080),
                            start = p1.position,
                            end = p2.position,
                            strokeWidth = 2.dp.toPx(),
                            pathEffect =
                                androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                    floatArrayOf(10f, 10f),
                                    0f,
                                ),
                        )
                    }
                }
            }
        }

        // Tooltip between nodes if 2 are selected
        if (selectedPersonIds.size == 2) {
            val p1 =
                (listOf(tree.centralPerson) + tree.persons).find { it.id == selectedPersonIds.first() }
            val p2 =
                (listOf(tree.centralPerson) + tree.persons).find { it.id == selectedPersonIds.last() }

            if (p1 != null && p2 != null) {
                val center1 = p1.position * scale + offset
                val center2 = p2.position * scale + offset
                val tooltipCenter = (center1 + center2) / 2f

                RelationshipTooltip(
                    modifier =
                        Modifier
                            .align(Alignment.TopStart)
                            .graphicsLayer {
                                translationX = tooltipCenter.x
                                translationY = tooltipCenter.y
                            }.pointerInput(Unit) {
                                // Prevent taps on tooltip from being handled by the canvas
                                detectTapGestures { }
                            },
                    onClick = onAddRelationshipClick,
                )
            }
        }
    }
}

private fun DrawScope.drawPerson(
    person: PersonNodeUi,
    textMeasurer: TextMeasurer,
    theme: ColorScheme,
    typography: Typography,
    isSelected: Boolean,
) {
    val nodeSize = 64.dp.toPx()
    val center = person.position
    val topLeft = Offset(center.x - nodeSize / 2, center.y - nodeSize / 2)

    if (isSelected) {
        drawPersonSelection(person, nodeSize, center, topLeft, theme.primary)
    }

    val color =
        when (person.biologicalSex) {
            Person.BiologicalSex.MALE -> Color(0xFFD1E4FF)
            Person.BiologicalSex.FEMALE -> Color(0xFFFFD1DC)
            else -> theme.secondary
        }

    drawPersonShape(person, color, nodeSize, center, topLeft)
    drawPersonIdentityMarkers(person, nodeSize, center, topLeft)
    drawPersonTextInfo(person, textMeasurer, typography, nodeSize, center, topLeft)
}

private fun DrawScope.drawPersonSelection(
    person: PersonNodeUi,
    nodeSize: Float,
    center: Offset,
    topLeft: Offset,
    color: Color,
) {
    val selectionPadding = 4.dp.toPx()
    val selectionSize = nodeSize + selectionPadding * 2
    val selectionTopLeft = Offset(topLeft.x - selectionPadding, topLeft.y - selectionPadding)

    when (person.biologicalSex) {
        Person.BiologicalSex.MALE -> {
            drawRect(
                color = color,
                topLeft = selectionTopLeft,
                size = Size(selectionSize, selectionSize),
                style = Stroke(width = 3.dp.toPx()),
            )
        }

        Person.BiologicalSex.FEMALE -> {
            drawCircle(
                color = color,
                center = center,
                radius = selectionSize / 2,
                style = Stroke(width = 3.dp.toPx()),
            )
        }

        else -> {
            drawRect(
                color = color,
                topLeft = selectionTopLeft,
                size = Size(selectionSize, selectionSize),
                style = Stroke(width = 3.dp.toPx()),
            )
        }
    }
}

private fun DrawScope.drawPersonShape(
    person: PersonNodeUi,
    color: Color,
    nodeSize: Float,
    center: Offset,
    topLeft: Offset,
) {
    when (person.biologicalSex) {
        Person.BiologicalSex.MALE -> {
            drawRect(color = color, topLeft = topLeft, size = Size(nodeSize, nodeSize))
            drawRect(
                color = Color.Black,
                topLeft = topLeft,
                size = Size(nodeSize, nodeSize),
                style = Stroke(width = 2f),
            )
            if (person.isIndexPerson) {
                val padding = 4.dp.toPx()
                drawRect(
                    color = Color.Black,
                    topLeft = topLeft + Offset(padding, padding),
                    size = Size(nodeSize - 2 * padding, nodeSize - 2 * padding),
                    style = Stroke(width = 2f),
                )
            }
        }

        Person.BiologicalSex.FEMALE -> {
            drawCircle(color = color, center = center, radius = nodeSize / 2)
            drawCircle(
                color = Color.Black,
                center = center,
                radius = nodeSize / 2,
                style = Stroke(width = 2f),
            )
            if (person.isIndexPerson) {
                val padding = 4.dp.toPx()
                drawCircle(
                    color = Color.Black,
                    center = center,
                    radius = nodeSize / 2 - padding,
                    style = Stroke(width = 2f),
                )
            }
        }

        else -> {
            drawRect(color = color, topLeft = topLeft, size = Size(nodeSize, nodeSize))
            drawRect(
                color = Color.Black,
                topLeft = topLeft,
                size = Size(nodeSize, nodeSize),
                style = Stroke(width = 2f),
            )
        }
    }
}

private fun DrawScope.drawPersonIdentityMarkers(
    person: PersonNodeUi,
    nodeSize: Float,
    center: Offset,
    topLeft: Offset,
) {
    if (person.sexualOrientation != Person.SexualOrientation.HETEROSEXUAL &&
        person.sexualOrientation != Person.SexualOrientation.UNKNOWN
    ) {
        val trianglePath =
            Path().apply {
                val triangleSize = nodeSize * 0.7f
                moveTo(center.x - triangleSize / 2, center.y - triangleSize / 3)
                lineTo(center.x + triangleSize / 2, center.y - triangleSize / 3)
                lineTo(center.x, center.y + triangleSize * 2 / 3)
                close()
            }
        drawPath(path = trianglePath, color = Color.Black, style = Stroke(width = 2f))
    }

    if (person.isDeceased) {
        drawLine(
            color = Color.Black,
            start = topLeft,
            end = topLeft + Offset(nodeSize, nodeSize),
            strokeWidth = 2f,
        )
        drawLine(
            color = Color.Black,
            start = topLeft + Offset(nodeSize, 0f),
            end = topLeft + Offset(0f, nodeSize),
            strokeWidth = 2f,
        )
    }
}

private fun DrawScope.drawPersonTextInfo(
    person: PersonNodeUi,
    textMeasurer: TextMeasurer,
    typography: Typography,
    nodeSize: Float,
    center: Offset,
    topLeft: Offset,
) {
    // Text: Name (below)
    val nameResult =
        textMeasurer.measure(
            text = "${person.firstName} ${person.lastName}",
            style =
                typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                ),
            constraints = Constraints(maxWidth = (nodeSize * 3).toInt()),
        )
    drawText(
        textLayoutResult = nameResult,
        topLeft =
            Offset(
                center.x - nameResult.size.width / 2,
                center.y + nodeSize / 2 + 8.dp.toPx(),
            ),
    )

    // Text: Birth date (top left)
    if (person.birthDateText.isNotEmpty()) {
        val birthResult =
            textMeasurer.measure(
                text = person.birthDateText,
                style = typography.bodySmall.copy(fontSize = 10.sp),
            )
        drawText(
            textLayoutResult = birthResult,
            topLeft =
                Offset(
                    topLeft.x - birthResult.size.width - 4.dp.toPx(),
                    topLeft.y - birthResult.size.height,
                ),
        )
    }

    // Text: Death date (top right)
    if (person.deathDateText.isNotEmpty()) {
        val deathResult =
            textMeasurer.measure(
                text = person.deathDateText,
                style = typography.bodySmall.copy(fontSize = 10.sp),
            )
        drawText(
            textLayoutResult = deathResult,
            topLeft =
                Offset(
                    topLeft.x + nodeSize + 4.dp.toPx(),
                    topLeft.y - deathResult.size.height,
                ),
        )
    }

    // Text: Age (inside)
    if (person.age.isNotEmpty()) {
        val ageResult =
            textMeasurer.measure(
                text = person.age,
                style =
                    typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
            )
        drawText(
            textLayoutResult = ageResult,
            topLeft =
                Offset(
                    center.x - ageResult.size.width / 2,
                    center.y - ageResult.size.height / 2,
                ),
        )
    }
}

@Composable
private fun RelationshipTooltip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val halfWidthPx = with(density) { 60.dp.toPx() }
    val halfHeightPx = with(density) { 16.dp.toPx() }

    Surface(
        onClick = onClick,
        modifier =
            modifier
                .height(32.dp)
                .graphicsLayer {
                    translationX -= halfWidthPx
                    translationY -= halfHeightPx
                },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.add_relationship),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black,
            )
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
                    position = Offset(500f, 750f),
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
                        position = Offset(500f, 400f),
                    ),
                    PersonNodeUi(
                        id = "3",
                        firstName = "Juan",
                        lastName = "García Pérez",
                        biologicalSex = Person.BiologicalSex.MALE,
                        sexualOrientation = Person.SexualOrientation.OTHER,
                        birthDateText = "2001",
                        age = "25",
                        position = Offset(500f, 1100f),
                    ),
                    PersonNodeUi(
                        id = "4",
                        firstName = "Sara",
                        lastName = "Salas De Mena",
                        biologicalSex = Person.BiologicalSex.FEMALE,
                        sexualOrientation = Person.SexualOrientation.OTHER,
                        birthDateText = "1990",
                        deathDateText = "2021",
                        age = "31",
                        isDeceased = true,
                        position = Offset(500f, 1500f),
                    ),
                ),
        )

    override val values =
        sequenceOf(
            TreeState(tree = seed),
            TreeState(
                tree = seed,
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
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
