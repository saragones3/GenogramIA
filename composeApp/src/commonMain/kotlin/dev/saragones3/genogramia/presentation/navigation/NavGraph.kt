package dev.saragones3.genogramia.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.saragones3.genogramia.presentation.authenticatedhome.AuthenticatedHomeScreen
import dev.saragones3.genogramia.presentation.guesthome.GuestHomeScreen
import dev.saragones3.genogramia.presentation.legends.LegendsScreen
import dev.saragones3.genogramia.presentation.login.LoginScreen
import dev.saragones3.genogramia.presentation.registration.RegistrationScreen
import dev.saragones3.genogramia.presentation.settings.SettingsScreen
import dev.saragones3.genogramia.presentation.splash.SplashScreen
import dev.saragones3.genogramia.presentation.splash.SplashViewModel
import dev.saragones3.genogramia.ui.theme.NavigationBarIndicator
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.nav_legends
import genogramia.composeapp.generated.resources.nav_settings
import genogramia.composeapp.generated.resources.nav_trees
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavGraph() {
    val backStack = rememberNavBackStack(initialKey = NavRoute.Splash)

    val currentRoute = backStack.lastOrNull()
    val isGuestMode = backStack.firstOrNull { it !is NavRoute.Splash } is NavRoute.GuestHome

    val showBottomBar =
        currentRoute is NavRoute.GuestHome ||
            currentRoute is NavRoute.AuthenticatedHome ||
            currentRoute is NavRoute.Legends ||
            currentRoute is NavRoute.Settings

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                ) {
                    val rootRoute = if (isGuestMode) NavRoute.GuestHome else NavRoute.AuthenticatedHome

                    val itemColors =
                        NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = NavigationBarIndicator,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                        )

                    NavigationBarItem(
                        selected = currentRoute is NavRoute.GuestHome || currentRoute is NavRoute.AuthenticatedHome,
                        onClick = {
                            if (currentRoute != rootRoute) {
                                backStack.clear()
                                backStack.add(rootRoute)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AccountTree,
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(Res.string.nav_trees),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        colors = itemColors,
                    )

                    NavigationBarItem(
                        selected = currentRoute is NavRoute.Legends,
                        onClick = {
                            if (currentRoute !is NavRoute.Legends) {
                                backStack.clear()
                                backStack.add(rootRoute)
                                backStack.add(NavRoute.Legends)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(Res.string.nav_legends),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        colors = itemColors,
                    )

                    if (!isGuestMode) {
                        NavigationBarItem(
                            selected = currentRoute is NavRoute.Settings,
                            onClick = {
                                if (currentRoute !is NavRoute.Settings) {
                                    backStack.clear()
                                    backStack.add(rootRoute)
                                    backStack.add(NavRoute.Settings)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(Res.string.nav_settings),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            },
                            colors = itemColors,
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) { key ->
            when (key) {
                is NavRoute.Splash -> {
                    val viewModel: SplashViewModel = koinViewModel()
                    val uiState by viewModel.uiState.collectAsState()

                    SplashScreen(
                        uiState = uiState,
                        onNavigateToGuestHome = {
                            backStack.clear()
                            backStack.add(NavRoute.GuestHome)
                        },
                        onNavigateToAuthenticatedHome = {
                            backStack.clear()
                            backStack.add(NavRoute.AuthenticatedHome)
                        },
                    )
                }
                is NavRoute.GuestHome -> {
                    GuestHomeScreen(
                        onLoginClick = { backStack.push(NavRoute.Login) },
                        onGoToTree = {},
                        onCreateTree = {},
                    )
                }
                is NavRoute.Login -> {
                    LoginScreen(
                        onBackClick = { backStack.pop() },
                        onLoginSuccess = {
                            backStack.clear()
                            backStack.add(NavRoute.AuthenticatedHome)
                        },
                    )
                }
                is NavRoute.AuthenticatedHome -> {
                    AuthenticatedHomeScreen()
                }
                is NavRoute.Legends -> {
                    LegendsScreen()
                }
                is NavRoute.Settings -> {
                    SettingsScreen()
                }
                is NavRoute.Registration -> {
                    RegistrationScreen(
                        onBackClick = { backStack.pop() },
                        onRegistrationSuccess = {
                            backStack.clear()
                            backStack.add(NavRoute.AuthenticatedHome)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun <T : Any> NavDisplay(
    backStack: List<T>,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    val currentKey = backStack.lastOrNull()
    if (currentKey != null) {
        Box(modifier = modifier) {
            content(currentKey)
        }
    }
}

private fun <T : Any> MutableList<T>.push(key: T) {
    add(key)
}

private fun <T : Any> MutableList<T>.pop() {
    if (size > 1) {
        removeAt(size - 1)
    }
}

@Composable
private fun rememberNavBackStack(initialKey: NavRoute): SnapshotStateList<NavRoute> =
    remember { mutableStateListOf(initialKey) }
