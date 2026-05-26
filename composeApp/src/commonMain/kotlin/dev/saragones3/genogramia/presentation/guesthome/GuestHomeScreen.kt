package dev.saragones3.genogramia.presentation.guesthome

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.app_name
import genogramia.composeapp.generated.resources.discover_stories
import genogramia.composeapp.generated.resources.explore_example
import genogramia.composeapp.generated.resources.guest_preview
import genogramia.composeapp.generated.resources.login
import genogramia.composeapp.generated.resources.preserve_heritage
import genogramia.composeapp.generated.resources.search_records
import genogramia.composeapp.generated.resources.start_first_tree
import genogramia.composeapp.generated.resources.start_first_tree_desc
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GuestHomeScreen(
    viewModel: GuestHomeViewModel = koinViewModel(),
    onLoginClick: () -> Unit,
    onGoToTree: (String) -> Unit,
    onCreateTree: () -> Unit,
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val trees by viewModel.trees.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onResume()
    }

    GuestHomeContent(
        searchQuery = searchQuery,
        trees = trees,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onLoginClick = onLoginClick,
        onGoToTree = onGoToTree,
        onCreateTree = onCreateTree,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuestHomeContent(
    searchQuery: String,
    trees: List<GenogramTree>,
    onSearchQueryChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onGoToTree: (String) -> Unit,
    onCreateTree: () -> Unit,
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

            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                placeholder = stringResource(Res.string.search_records),
            )

            Spacer(modifier = Modifier.height(40.dp))

            GuestHomeTitleSection()

            Spacer(modifier = Modifier.height(40.dp))

            trees.forEach { tree ->
                GenogramTreeCard(
                    title = tree.name,
                    ancestorCount = tree.ancestorCount,
                    lastUpdated = tree.lastUpdated,
                    buttonText = stringResource(Res.string.explore_example),
                    onButtonClick = { onGoToTree(tree.id) },
                    badgeText = stringResource(Res.string.guest_preview),
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            AddTreeCard(
                title = stringResource(Res.string.start_first_tree),
                subtitle = stringResource(Res.string.start_first_tree_desc),
                onClick = onCreateTree,
            )
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

@Preview
@Composable
private fun GuestHomeScreenPreview() {
    GenogramiaTheme {
        GuestHomeContent(
            searchQuery = "",
            trees = emptyList(),
            onSearchQueryChange = {},
            onLoginClick = {},
            onGoToTree = {},
            onCreateTree = {},
        )
    }
}
