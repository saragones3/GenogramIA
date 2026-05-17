package dev.saragones3.genogramia.presentation.tree

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import genogramia.composeapp.generated.resources.Res
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
    onAddPersonClick: (String) -> Unit,
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
        onAddPersonClick = { onAddPersonClick(treeId) },
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
            FloatingActionButton(
                onClick = onAddPersonClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
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
                onTransform = { pan, zoom ->
                    onEvent(TreeEvent.OnTransform(pan, zoom))
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
    onTransform: (Offset, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val theme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clipToBounds()
                .pointerInput(Unit) {
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
                drawPerson(tree.centralPerson, textMeasurer, theme, typography)
                tree.persons.forEach { person ->
                    drawPerson(person, textMeasurer, theme, typography)
                }
            }
        }
    }
}

private fun DrawScope.drawPerson(
    person: PersonUi,
    textMeasurer: TextMeasurer,
    theme: ColorScheme,
    typography: Typography,
) {
    val nodeSize = 64.dp.toPx()
    val center = person.position
    val topLeft = Offset(center.x - nodeSize / 2, center.y - nodeSize / 2)

    val color =
        when (person.biologicalSex) {
            Person.BiologicalSex.MALE -> Color(0xFFD1E4FF)
            Person.BiologicalSex.FEMALE -> Color(0xFFFFD1DC)
            else -> theme.secondary
        }

    // Draw Shape
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

    // LGBT Triangle
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

    // Death X
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
                PersonUi(
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
                    PersonUi(
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
                    PersonUi(
                        id = "3",
                        firstName = "Juan",
                        lastName = "García Pérez",
                        biologicalSex = Person.BiologicalSex.MALE,
                        sexualOrientation = Person.SexualOrientation.OTHER,
                        birthDateText = "2001",
                        age = "25",
                        position = Offset(500f, 1100f),
                    ),
                    PersonUi(
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
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
