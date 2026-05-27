package dev.saragones3.genogramia.presentation.authenticatedhome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.saragones3.genogramia.presentation.components.AddTreeCard
import dev.saragones3.genogramia.presentation.components.GenogramTreeCard
import dev.saragones3.genogramia.presentation.components.GenogramTreeCardSkeleton
import dev.saragones3.genogramia.presentation.components.SearchBar
import dev.saragones3.genogramia.presentation.model.GenogramTreeUiModel
import dev.saragones3.genogramia.presentation.util.UiText
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.app_name
import genogramia.composeapp.generated.resources.auth_home_open_archive
import genogramia.composeapp.generated.resources.auth_home_primary_lineage
import genogramia.composeapp.generated.resources.auth_home_search_hint
import genogramia.composeapp.generated.resources.auth_home_start_tree
import genogramia.composeapp.generated.resources.auth_home_start_tree_subtitle
import genogramia.composeapp.generated.resources.auth_home_subtitle
import genogramia.composeapp.generated.resources.auth_home_welcome
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthenticatedHomeScreen(
    onCreateTreeClick: () -> Unit,
    onOpenTreeClick: (String) -> Unit,
) {
    val viewModel: AuthenticatedHomeViewModel = koinViewModel()
    val userName by viewModel.userName.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val trees by viewModel.trees.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onResume()
    }

    AuthenticatedHomeContent(
        userName = userName ?: "",
        searchQuery = searchQuery,
        isLoading = isLoading,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        trees = trees,
        onCreateTreeClick = onCreateTreeClick,
        onOpenTreeClick = onOpenTreeClick,
    )
}

@Composable
private fun AuthenticatedHomeContent(
    userName: String,
    searchQuery: String,
    isLoading: Boolean,
    trees: List<GenogramTreeUiModel>,
    onSearchQueryChange: (String) -> Unit,
    onCreateTreeClick: () -> Unit,
    onOpenTreeClick: (String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                    )
                    // Avatar placeholder
                    Box(
                        modifier =
                            Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0)),
                    )
                }
            }

            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    placeholder = stringResource(Res.string.auth_home_search_hint),
                )
            }

            item {
                Column {
                    Text(
                        text = stringResource(Res.string.auth_home_welcome, userName),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(Res.string.auth_home_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray.copy(alpha = 0.8f),
                        lineHeight = 24.sp,
                    )
                }
            }

            item {
                AnimatedContent(
                    targetState = isLoading,
                    transitionSpec = {
                        fadeIn().togetherWith(fadeOut())
                    },
                    label = "trees_loading_transition",
                ) { loading ->
                    if (loading) {
                        Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
                            repeat(2) {
                                GenogramTreeCardSkeleton()
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
                            trees.forEach { tree ->
                                GenogramTreeCard(
                                    title = tree.title,
                                    ancestorCount = tree.ancestorCount,
                                    lastUpdated = tree.lastUpdated,
                                    buttonText = stringResource(Res.string.auth_home_open_archive),
                                    onButtonClick = { onOpenTreeClick(tree.id) },
                                    badgeText = if (tree.isPrimary) stringResource(Res.string.auth_home_primary_lineage) else null,
                                )
                            }

                            AddTreeCard(
                                title = stringResource(Res.string.auth_home_start_tree),
                                subtitle = stringResource(Res.string.auth_home_start_tree_subtitle),
                                onClick = onCreateTreeClick,
                            )
                        }
                    }
                }
            }
        }
    }
}

private class GenogramTreeUiModelProvider : PreviewParameterProvider<List<GenogramTreeUiModel>> {

    override val values =
        sequenceOf(
            listOf(
                GenogramTreeUiModel(
                    id = "1",
                    title = "Aragones Family",
                    ancestorCount = 1240,
                    lastUpdated = UiText.DynamicString("2 days ago"),
                    isPrimary = true,
                ),
            ),
            emptyList(),
        )
}

@Composable
@Preview
private fun AuthenticatedHomeScreenPreview(
    @PreviewParameter(GenogramTreeUiModelProvider::class) trees: List<GenogramTreeUiModel>,
) {
    GenogramiaTheme {
        AuthenticatedHomeContent(
            userName = "Sergio",
            searchQuery = "",
            isLoading = false,
            onSearchQueryChange = {},
            trees = trees,
            onCreateTreeClick = {},
            onOpenTreeClick = {},
        )
    }
}

@Composable
@Preview
private fun LoadingAuthenticatedHomeScreenPreview() {
    GenogramiaTheme {
        AuthenticatedHomeContent(
            userName = "Sergio",
            searchQuery = "",
            isLoading = true,
            onSearchQueryChange = {},
            trees = emptyList(),
            onCreateTreeClick = {},
            onOpenTreeClick = {},
        )
    }
}
