package dev.saragones3.genogramia.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary
import dev.saragones3.genogramia.ui.theme.SurfaceContainerLow
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.settings_cancel
import genogramia.composeapp.generated.resources.settings_change_password
import genogramia.composeapp.generated.resources.settings_change_password_desc
import genogramia.composeapp.generated.resources.settings_confirm
import genogramia.composeapp.generated.resources.settings_delete_account
import genogramia.composeapp.generated.resources.settings_delete_confirmation_message
import genogramia.composeapp.generated.resources.settings_delete_confirmation_title
import genogramia.composeapp.generated.resources.settings_log_out
import genogramia.composeapp.generated.resources.settings_logout_confirmation_message
import genogramia.composeapp.generated.resources.settings_logout_confirmation_title
import genogramia.composeapp.generated.resources.settings_security_section
import genogramia.composeapp.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onChangePasswordClick: () -> Unit,
    onLoggedOut: () -> Unit,
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLoggedOut()
            viewModel.logoutConsumed()
        }
    }

    SettingsContent(
        state = state,
        onEvent = viewModel::onEvent,
        onChangePasswordClick = onChangePasswordClick,
    )

    if (state.showLogoutConfirmation) {
        SettingsConfirmationDialog(
            title = stringResource(Res.string.settings_logout_confirmation_title),
            message = stringResource(Res.string.settings_logout_confirmation_message),
            onConfirm = { viewModel.onEvent(SettingsEvent.OnLogoutConfirmed) },
            onDismiss = { viewModel.onEvent(SettingsEvent.OnDismissDialogs) },
        )
    }

    if (state.showDeleteConfirmation) {
        SettingsConfirmationDialog(
            title = stringResource(Res.string.settings_delete_confirmation_title),
            message = stringResource(Res.string.settings_delete_confirmation_message),
            onConfirm = { viewModel.onEvent(SettingsEvent.OnDeleteConfirmed) },
            onDismiss = { viewModel.onEvent(SettingsEvent.OnDismissDialogs) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    onChangePasswordClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(Res.string.settings_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                        )
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.padding(16.dp),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            ProfileCard(user = state.user)

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle(stringResource(Res.string.settings_security_section))

            Spacer(modifier = Modifier.height(16.dp))

            SettingsItem(
                icon = Icons.Default.VpnKey,
                title = stringResource(Res.string.settings_change_password),
                subtitle = stringResource(Res.string.settings_change_password_desc),
                onClick = onChangePasswordClick,
            )

            Spacer(modifier = Modifier.height(48.dp))

            LogoutButton(onClick = { onEvent(SettingsEvent.OnLogOutClicked) })

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(Res.string.settings_delete_account),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.clickable { onEvent(SettingsEvent.OnDeleteAccountClicked) },
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileCard(user: User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Avatar Placeholder (matching design circle)
            Box(
                modifier =
                    Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Primary,
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = user?.displayName ?: "Unknown User",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = user?.email ?: "no-email@example.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLow),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.LightGray,
            )
        }
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .clickable { onClick() }
                .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.Logout,
            contentDescription = null,
            tint = Color(0xFFD32F2F),
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(Res.string.settings_log_out),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD32F2F),
        )
    }
}

@Composable
private fun SettingsConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(Res.string.settings_confirm), color = Primary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.settings_cancel), color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
    )
}

@Composable
@Preview
fun SettingsScreenPreview() {
    GenogramiaTheme {
        SettingsContent(
            state =
                SettingsState(
                    user = User("1", "ana.garcia@example.com", "Ana García"),
                ),
            onEvent = {},
            onChangePasswordClick = {},
        )
    }
}
