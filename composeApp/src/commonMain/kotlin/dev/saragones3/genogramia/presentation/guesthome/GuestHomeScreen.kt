package dev.saragones3.genogramia.presentation.guesthome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.app_name
import genogramia.composeapp.generated.resources.background_tree
import genogramia.composeapp.generated.resources.discover_stories
import genogramia.composeapp.generated.resources.explore_example
import genogramia.composeapp.generated.resources.guest_preview
import genogramia.composeapp.generated.resources.login
import genogramia.composeapp.generated.resources.preserve_heritage
import genogramia.composeapp.generated.resources.sample_tree
import genogramia.composeapp.generated.resources.sample_tree_desc
import genogramia.composeapp.generated.resources.search_records
import genogramia.composeapp.generated.resources.start_first_tree
import genogramia.composeapp.generated.resources.start_first_tree_desc
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestHomeScreen(
    onLoginClick: () -> Unit,
    onGoToTree: () -> Unit,
    onCreateTree: () -> Unit,
    viewModel: GuestHomeViewModel = koinViewModel(),
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            GuestHomeTopBar(onLoginClick = onLoginClick)
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            GuestHomeSearchBar()

            Spacer(modifier = Modifier.height(40.dp))

            GuestHomeTitleSection()

            Spacer(modifier = Modifier.height(40.dp))

            GuestHomeSampleTreeCard(onGoToTree = onGoToTree)

            Spacer(modifier = Modifier.height(20.dp))

            GuestHomeStartTreeCard(onCreateTree = onCreateTree)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuestHomeTopBar(onLoginClick: () -> Unit) {
    TopAppBar(
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
        title = {
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        actions = {
            Button(
                onClick = onLoginClick,
                modifier = Modifier.padding(end = 8.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                    ),
                shape = CircleShape,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(Res.string.login),
                    fontWeight = FontWeight.Bold,
                )
            }
        },
    )
}

@Composable
private fun GuestHomeSearchBar() {
    var searchQuery by remember { mutableStateOf("") }

    TextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = stringResource(Res.string.search_records),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        },
        shape = CircleShape,
        singleLine = true,
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
    )
}

@Composable
private fun GuestHomeTitleSection() {
    Column {
        Text(
            text = stringResource(Res.string.preserve_heritage),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 44.sp,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.discover_stories),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            lineHeight = 24.sp,
        )
    }
}

@Composable
private fun GuestHomeSampleTreeCard(onGoToTree: () -> Unit) {
    Surface(
        onClick = onGoToTree,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(260.dp),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.primary,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(Res.drawable.background_tree),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            brush =
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                        ),
                                ),
                        ).padding(24.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                    ) {
                        Text(
                            text = stringResource(Res.string.guest_preview).uppercase(),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(Res.string.sample_tree),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(Res.string.sample_tree_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 2,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onGoToTree,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.explore_example),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuestHomeStartTreeCard(onCreateTree: () -> Unit) {
    Surface(
        onClick = onCreateTree,
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .dashedBorder(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(32.dp),
                    on = 8.dp,
                    off = 8.dp,
                ),
        color = Color(0xFFE0F2F1).copy(alpha = 0.3f),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(Res.string.start_first_tree),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.start_first_tree_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    shape: Shape,
    on: Dp,
    off: Dp,
): Modifier =
    this.drawBehind {
        val outline = shape.createOutline(size, layoutDirection, this)
        val path =
            Path().apply {
                addOutline(outline)
            }
        val stroke =
            Stroke(
                width = width.toPx(),
                pathEffect =
                    PathEffect.dashPathEffect(
                        intervals = floatArrayOf(on.toPx(), off.toPx()),
                        phase = 0f,
                    ),
            )
        drawPath(
            path = path,
            color = color,
            style = stroke,
        )
    }

@Preview
@Composable
fun GuestHomeScreenPreview() {
    GenogramiaTheme {
        GuestHomeScreen(
            onLoginClick = {},
            onGoToTree = {},
            onCreateTree = {},
        )
    }
}
