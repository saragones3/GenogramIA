package dev.saragones3.genogramia.presentation.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.ShapeFull
import dev.saragones3.genogramia.ui.theme.SurfaceContainerHighest
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.error_empty_fields
import genogramia.composeapp.generated.resources.error_invalid_email
import genogramia.composeapp.generated.resources.error_unknown
import genogramia.composeapp.generated.resources.error_user_not_found
import genogramia.composeapp.generated.resources.forgot_password_button
import genogramia.composeapp.generated.resources.forgot_password_email_hint
import genogramia.composeapp.generated.resources.forgot_password_email_label
import genogramia.composeapp.generated.resources.forgot_password_subtitle
import genogramia.composeapp.generated.resources.forgot_password_success_message
import genogramia.composeapp.generated.resources.forgot_password_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    initialEmail: String? = null,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onSuccessConsumed: () -> Unit,
    onErrorShown: () -> Unit,
    onBackClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(initialEmail) {
        initialEmail?.let { onEmailChange(it) }
    }

    val successMessage = stringResource(Res.string.forgot_password_success_message)
    val userNotFoundStr = stringResource(Res.string.error_user_not_found)
    val unknownErrorStr = stringResource(Res.string.error_unknown)

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar(successMessage)
            onSuccessConsumed()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            val message =
                when (error) {
                    ForgotPasswordState.ForgotPasswordError.UserNotFound -> userNotFoundStr
                    ForgotPasswordState.ForgotPasswordError.Generic -> unknownErrorStr
                }
            snackbarHostState.showSnackbar(message)
            onErrorShown()
        }
    }

    ForgotPasswordContent(
        state = state,
        isEmailFixed = initialEmail != null,
        onEmailChange = onEmailChange,
        onSendClick = onSendClick,
        onBackClick = onBackClick,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForgotPasswordContent(
    state: ForgotPasswordState,
    isEmailFixed: Boolean,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
            ) {
                Column {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .background(MaterialTheme.colorScheme.primary),
                    )

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp)
                                .padding(top = 32.dp, bottom = 24.dp),
                    ) {
                        ForgotPasswordHeader()

                        Spacer(modifier = Modifier.height(32.dp))

                        if (isEmailFixed) {
                            Text(
                                text = state.email,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                            )
                        } else {
                            ForgotPasswordForm(
                                state = state,
                                onEmailChange = onEmailChange,
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        SendButton(
                            isLoading = state.isLoading,
                            onClick = onSendClick,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ForgotPasswordHeader() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.VpnKey,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(Res.string.forgot_password_title),
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = stringResource(Res.string.forgot_password_subtitle),
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun ForgotPasswordForm(
    state: ForgotPasswordState,
    onEmailChange: (String) -> Unit,
) {
    val emptyFieldStr = stringResource(Res.string.error_empty_fields)
    val invalidEmailStr = stringResource(Res.string.error_invalid_email)

    val emailErrorText =
        when (state.emailError) {
            ForgotPasswordState.ValidationError.EMPTY -> emptyFieldStr
            ForgotPasswordState.ValidationError.INVALID -> invalidEmailStr
            null -> null
        }

    Text(
        text = stringResource(Res.string.forgot_password_email_label),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    SoftTextField(
        value = state.email,
        onValueChange = onEmailChange,
        placeholder = stringResource(Res.string.forgot_password_email_hint),
        leadingIcon = Icons.Default.Email,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        errorText = emailErrorText,
    )
}

@Composable
private fun SendButton(
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(60.dp),
        shape = ShapeFull,
        enabled = !isLoading,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(end = 8.dp).size(24.dp),
                strokeWidth = 2.dp,
            )
        }
        Text(
            text = stringResource(Res.string.forgot_password_button),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun SoftTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    errorText: String? = null,
) {
    var isFocused by remember { mutableStateOf(false) }
    val isError = errorText != null

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray) },
        isError = isError,
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .onFocusChanged { isFocused = it.isFocused }
                .then(
                    if (isFocused) {
                        Modifier.border(
                            width = 1.dp,
                            color =
                                if (isError) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                },
                            shape = RoundedCornerShape(16.dp),
                        )
                    } else {
                        Modifier
                    },
                ),
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
        leadingIcon =
            leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isError) MaterialTheme.colorScheme.error else Color.Gray,
                    )
                }
            },
        supportingText =
            if (isError) {
                {
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            } else {
                null
            },
        keyboardOptions = keyboardOptions,
        singleLine = true,
    )
}

@Composable
@Preview
private fun ForgotPasswordScreenPreview() {
    GenogramiaTheme {
        ForgotPasswordContent(
            state = ForgotPasswordState(email = "test@example.com"),
            isEmailFixed = false,
            onEmailChange = {},
            onSendClick = {},
            onBackClick = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
