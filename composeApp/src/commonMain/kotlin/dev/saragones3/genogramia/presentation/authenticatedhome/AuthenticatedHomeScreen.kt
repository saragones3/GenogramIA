package dev.saragones3.genogramia.presentation.authenticatedhome

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.presentation.components.AddTreeCard
import dev.saragones3.genogramia.presentation.components.GenogramTreeCard
import dev.saragones3.genogramia.presentation.components.SearchBar
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.app_name
import genogramia.composeapp.generated.resources.auth_home_ancestors
import genogramia.composeapp.generated.resources.auth_home_open_archive
import genogramia.composeapp.generated.resources.auth_home_primary_lineage
import genogramia.composeapp.generated.resources.auth_home_search_hint
import genogramia.composeapp.generated.resources.auth_home_start_tree
import genogramia.composeapp.generated.resources.auth_home_start_tree_subtitle
import genogramia.composeapp.generated.resources.auth_home_subtitle
import genogramia.composeapp.generated.resources.auth_home_welcome
import org.jetbrains.compose.resources.stringResource

@Composable
fun AuthenticatedHomeScreen(
    userName: String,
    searchQuery: String,
    trees: List<GenogramTree>,
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

            items(trees, key = { it.id }) { tree ->
                GenogramTreeCard(
                    title = tree.name,
                    description = stringResource(Res.string.auth_home_ancestors, tree.ancestorCount, tree.lastUpdated),
                    buttonText = stringResource(Res.string.auth_home_open_archive),
                    onButtonClick = { onOpenTreeClick(tree.id) },
                    badgeText = if (tree.id == "1") stringResource(Res.string.auth_home_primary_lineage) else null,
                )
            }

            item {
                AddTreeCard(
                    title = stringResource(Res.string.auth_home_start_tree),
                    subtitle = stringResource(Res.string.auth_home_start_tree_subtitle),
                    onClick = onCreateTreeClick,
                )
            }
        }
    }
}

@Composable
@Preview
private fun AuthenticatedHomeScreenPreview() {
    GenogramiaTheme {
        AuthenticatedHomeScreen(
            userName = "Sergio",
            searchQuery = "",
            onSearchQueryChange = {},
            trees =
                listOf(
                    GenogramTree("1", "Aragones Family", 1240, "2 days ago"),
                ),
            onCreateTreeClick = {},
            onOpenTreeClick = {},
        )
    }
}
