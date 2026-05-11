package dev.saragones3.genogramia.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import dev.saragones3.genogramia.presentation.changepassword.ChangePasswordScreen
import dev.saragones3.genogramia.presentation.forgotpassword.ForgotPasswordScreen
import dev.saragones3.genogramia.presentation.guesthome.GuestHomeScreen
import dev.saragones3.genogramia.presentation.legends.LegendsScreen
import dev.saragones3.genogramia.presentation.login.LoginScreen
import dev.saragones3.genogramia.presentation.registration.RegistrationScreen
import dev.saragones3.genogramia.presentation.settings.SettingsScreen
import dev.saragones3.genogramia.presentation.splash.SplashScreen
import dev.saragones3.genogramia.ui.theme.NavigationBarIndicator
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.nav_legends
import genogramia.composeapp.generated.resources.nav_settings
import genogramia.composeapp.generated.resources.nav_trees
import org.jetbrains.compose.resources.stringResource

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
                AppBottomBar(
                    currentRoute = currentRoute,
                    isGuestMode = isGuestMode,
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            backStack.clear()
                            if (route is NavRoute.Legends || route is NavRoute.Settings) {
                                val root = if (isGuestMode) NavRoute.GuestHome else NavRoute.AuthenticatedHome
                                backStack.add(root)
                            }
                            backStack.add(route)
                        }
                    },
                )
            }
        },
    ) { padding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) { key ->
            when (key) {
                is NavRoute.Splash -> {
                    SplashScreen(
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
                        onLoginSuccess = {
                            backStack.clear()
                            backStack.add(NavRoute.AuthenticatedHome)
                        },
                        onRegisterClick = {
                            backStack.push(NavRoute.Registration)
                        },
                        onForgotPasswordClick = {
                            backStack.push(NavRoute.ForgotPassword())
                        },
                        onBackClick = { backStack.pop() },
                    )
                }

                is NavRoute.ForgotPassword -> {
                    ForgotPasswordScreen(
                        initialEmail = key.email,
                        onBackClick = { backStack.pop() },
                    )
                }

                is NavRoute.AuthenticatedHome -> {
                    AuthenticatedHomeScreen(
                        onCreateTreeClick = { /* US-011 */ },
                        onOpenTreeClick = { /* US-012 */ },
                    )
                }

                is NavRoute.Legends -> {
                    LegendsScreen()
                }

                is NavRoute.Settings -> {
                    SettingsScreen(
                        onChangePasswordClick = {
                            backStack.push(NavRoute.ChangePassword)
                        },
                        onLoggedOut = {
                            backStack.clear()
                            backStack.add(NavRoute.GuestHome)
                        },
                    )
                }

                is NavRoute.ChangePassword -> {
                    ChangePasswordScreen(
                        onBackClick = { backStack.pop() },
                        onForgotPasswordClick = {
                            backStack.push(NavRoute.ForgotPassword(it))
                        },
                    )
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
private fun AppBottomBar(
    currentRoute: NavRoute,
    isGuestMode: Boolean,
    onNavigate: (NavRoute) -> Unit,
) {
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
            onClick = { onNavigate(rootRoute) },
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
            onClick = { onNavigate(NavRoute.Legends) },
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
                selected = currentRoute is NavRoute.Settings || currentRoute is NavRoute.ChangePassword,
                onClick = { onNavigate(NavRoute.Settings) },
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
