package dev.saragones3.genogramia.presentation.settings

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary
import dev.saragones3.genogramia.ui.theme.SurfaceContainerHighest
import dev.saragones3.genogramia.ui.theme.SurfaceContainerLow
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.change_password_confirm_label
import genogramia.composeapp.generated.resources.change_password_current_label
import genogramia.composeapp.generated.resources.change_password_data_protection_desc
import genogramia.composeapp.generated.resources.change_password_data_protection_title
import genogramia.composeapp.generated.resources.change_password_forgot
import genogramia.composeapp.generated.resources.change_password_new_label
import genogramia.composeapp.generated.resources.change_password_save
import genogramia.composeapp.generated.resources.change_password_settings
import genogramia.composeapp.generated.resources.change_password_subtitle
import genogramia.composeapp.generated.resources.change_password_success
import genogramia.composeapp.generated.resources.change_password_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChangePasswordScreen(
    state: ChangePasswordState,
    onDataChange: (String, String, String) -> Unit,
    onSaveClick: () -> Unit,
    onSuccessConsumed: () -> Unit,
    onBackClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(Res.string.change_password_success)

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar(successMessage)
            onSuccessConsumed()
        }
    }

    ChangePasswordContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onDataChange = onDataChange,
        onSaveClick = onSaveClick,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordContent(
    state: ChangePasswordState,
    snackbarHostState: SnackbarHostState,
    onDataChange: (String, String, String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.change_password_settings),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null,
                                tint = Primary,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header Section
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(Res.string.change_password_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.change_password_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 24.sp,
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Info Card
            DataProtectionCard()

            Spacer(modifier = Modifier.height(40.dp))

            // Form Section
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            ) {
                var currentPasswordVisible by remember { mutableStateOf(false) }
                PasswordField(
                    label = stringResource(Res.string.change_password_current_label),
                    value = state.currentPassword,
                    onValueChange = { onDataChange(it, state.newPassword, state.confirmPassword) },
                    isVisible = currentPasswordVisible,
                    onToggleVisibility = { currentPasswordVisible = !currentPasswordVisible },
                    trailingIcon = Icons.Default.Lock,
                    error = state.currentPasswordError?.message?.let { stringResource(it) },
                    imeAction = ImeAction.Next,
                )

                Spacer(modifier = Modifier.height(32.dp))

                var newPasswordVisible by remember { mutableStateOf(false) }
                PasswordField(
                    label = stringResource(Res.string.change_password_new_label),
                    value = state.newPassword,
                    onValueChange = { onDataChange(state.currentPassword, it, state.confirmPassword) },
                    isVisible = newPasswordVisible,
                    onToggleVisibility = { newPasswordVisible = !newPasswordVisible },
                    trailingIcon = Icons.Default.Lock,
                    error = state.passwordError?.message?.let { stringResource(it) },
                    imeAction = ImeAction.Next,
                )

                Spacer(modifier = Modifier.height(32.dp))

                var confirmPasswordVisible by remember { mutableStateOf(false) }
                PasswordField(
                    label = stringResource(Res.string.change_password_confirm_label),
                    value = state.confirmPassword,
                    onValueChange = { onDataChange(state.currentPassword, state.newPassword, it) },
                    isVisible = confirmPasswordVisible,
                    onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                    trailingIcon = Icons.Default.CheckCircle,
                    error = state.confirmError?.message?.let { stringResource(it) },
                    imeAction = ImeAction.Done,
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { /* TODO: US-005 */ },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(
                        text = stringResource(Res.string.change_password_forgot),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (state.generalError != null) {
                Text(
                    text = stringResource(state.generalError),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Save Button
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(64.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = !state.isLoading,
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(Res.string.change_password_save),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DataProtectionCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = Primary.copy(alpha = 0.1f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = stringResource(Res.string.change_password_data_protection_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.change_password_data_protection_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    trailingIcon: ImageVector,
    error: String? = null,
    imeAction: ImeAction = ImeAction.Default,
) {
    val isError = error != null

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 12.dp),
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("........", color = Color.Gray, fontSize = 18.sp) },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleVisibility) {
                        val visibilityIcon = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        Icon(imageVector = visibilityIcon, contentDescription = null, tint = Color.Gray)
                    }
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = if (isError) MaterialTheme.colorScheme.error else Color.LightGray,
                        modifier = Modifier.padding(end = 12.dp).size(20.dp),
                    )
                }
            },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
            singleLine = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(16.dp)),
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceContainerHighest,
                    unfocusedContainerColor = SurfaceContainerHighest,
                    errorContainerColor = SurfaceContainerHighest,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                ),
            isError = isError,
        )
        if (isError) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )
        }
    }
}

@Composable
@Preview
private fun ChangePasswordScreenPreview() {
    GenogramiaTheme {
        ChangePasswordContent(
            state = ChangePasswordState(),
            snackbarHostState = remember { SnackbarHostState() },
            onDataChange = { _, _, _ -> },
            onSaveClick = {},
            onBackClick = {},
        )
    }
}
