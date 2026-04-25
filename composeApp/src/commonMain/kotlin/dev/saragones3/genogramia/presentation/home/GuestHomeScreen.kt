package dev.saragones3.genogramia.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.saragones3.genogramia.ui.theme.GenogramiaColors
import genogramia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestHomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSampleTree: () -> Unit,
    onNavigateToCreateTree: () -> Unit
) {
    Scaffold(
        containerColor = GenogramiaColors.Surface,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        text = stringResource(Res.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GenogramiaColors.Primary
                    )
                },
                actions = {
                    Button(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GenogramiaColors.Primary,
                            contentColor = Color.White
                        ),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.login),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = CircleShape,
                color = GenogramiaColors.SurfaceContainerLowest,
                shadowElevation = 8.dp
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(80.dp)
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = {
                            Icon(Icons.Filled.AccountTree, contentDescription = null)
                        },
                        label = { 
                            Text(
                                text = stringResource(Res.string.trees).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            ) 
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GenogramiaColors.Primary,
                            selectedTextColor = GenogramiaColors.Primary,
                            indicatorColor = GenogramiaColors.Primary.copy(alpha = 0.1f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = {
                            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                        },
                        label = { 
                            Text(
                                text = stringResource(Res.string.legends).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            ) 
                        },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar (Soft Inset Pill)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                color = GenogramiaColors.SearchBackground
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = GenogramiaColors.Primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(Res.string.search_records),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title & Description
            Text(
                text = stringResource(Res.string.preserve_heritage),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = GenogramiaColors.OnSurface,
                lineHeight = 44.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.discover_stories),
                style = MaterialTheme.typography.bodyLarge,
                color = GenogramiaColors.OnSurface.copy(alpha = 0.7f),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Sample Tree Card (Gradient Hero)
            Surface(
                onClick = onNavigateToSampleTree,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                shape = RoundedCornerShape(32.dp),
                color = GenogramiaColors.Primary
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    GenogramiaColors.Primary,
                                    GenogramiaColors.PrimaryContainer
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = stringResource(Res.string.guest_preview).uppercase(),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = stringResource(Res.string.sample_tree),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = stringResource(Res.string.sample_tree_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 2
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Button(
                            onClick = onNavigateToSampleTree,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = GenogramiaColors.Primary
                            ),
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.explore_example),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Start First Tree Card (Dashed Border)
            Surface(
                onClick = onNavigateToCreateTree,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .dashedBorder(
                        width = 2.dp,
                        color = GenogramiaColors.Primary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(32.dp),
                        on = 8.dp,
                        off = 8.dp
                    ),
                color = Color(0xFFE0F2F1).copy(alpha = 0.3f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = GenogramiaColors.Primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = stringResource(Res.string.start_first_tree),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GenogramiaColors.OnSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = stringResource(Res.string.start_first_tree_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = GenogramiaColors.OnSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    shape: Shape,
    on: Dp,
    off: Dp
): Modifier = this.drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    val path = Path().apply {
        addOutline(outline)
    }
    val stroke = Stroke(
        width = width.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(on.toPx(), off.toPx()),
            phase = 0f
        )
    )
    drawPath(
        path = path,
        color = color,
        style = stroke
    )
}

@Preview
@Composable
fun GuestHomeScreenPreview() {
    MaterialTheme {
        GuestHomeScreen(
            onNavigateToLogin = {},
            onNavigateToSampleTree = {},
            onNavigateToCreateTree = {}
        )
    }
}
