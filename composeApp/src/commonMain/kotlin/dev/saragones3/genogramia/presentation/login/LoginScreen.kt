package dev.saragones3.genogramia.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.ShapeFull
import dev.saragones3.genogramia.ui.theme.SurfaceContainerHighest
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.error_empty_fields
import genogramia.composeapp.generated.resources.error_invalid_credentials
import genogramia.composeapp.generated.resources.error_invalid_email
import genogramia.composeapp.generated.resources.error_invalid_password
import genogramia.composeapp.generated.resources.error_user_not_found
import genogramia.composeapp.generated.resources.login_button
import genogramia.composeapp.generated.resources.login_create_account_label
import genogramia.composeapp.generated.resources.login_email_hint
import genogramia.composeapp.generated.resources.login_email_label
import genogramia.composeapp.generated.resources.login_forgot_password
import genogramia.composeapp.generated.resources.login_guest_label
import genogramia.composeapp.generated.resources.login_no_account
import genogramia.composeapp.generated.resources.login_or_continue
import genogramia.composeapp.generated.resources.login_password_hint
import genogramia.composeapp.generated.resources.login_password_label
import genogramia.composeapp.generated.resources.login_register_link
import genogramia.composeapp.generated.resources.login_subtitle
import genogramia.composeapp.generated.resources.login_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onDataChange: (String, String) -> Unit,
    onLoginClick: () -> Unit,
    onErrorShown: () -> Unit,
    onLoginSuccessConsumed: () -> Unit,
    onBackClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onGuestClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val invalidCredentialsStr = stringResource(Res.string.error_invalid_credentials)
    val userNotFoundStr = stringResource(Res.string.error_user_not_found)

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
            onLoginSuccessConsumed()
        }
    }

    LaunchedEffect(uiState.generalError) {
        uiState.generalError?.let { error ->
            val message =
                when (error) {
                    LoginError.WrongCredentials -> invalidCredentialsStr
                    LoginError.UserNotFound -> userNotFoundStr
                }
            snackbarHostState.showSnackbar(message)
            onErrorShown()
        }
    }

    LoginContent(
        uiState = uiState,
        onDataChange = onDataChange,
        onLoginClick = onLoginClick,
        onBackClick = onBackClick,
        onRegisterClick = onRegisterClick,
        onForgotPasswordClick = {},
        onGuestClick = onGuestClick,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onDataChange: (String, String) -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGuestClick: () -> Unit,
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
                        LoginHeader()

                        Spacer(modifier = Modifier.height(32.dp))

                        LoginForm(
                            uiState = uiState,
                            onDataChange = onDataChange,
                            onForgotPasswordClick = onForgotPasswordClick,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        LoginButton(
                            isLoading = uiState.isLoading,
                            onClick = onLoginClick,
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        OrContinueWithDivider()

                        Spacer(modifier = Modifier.height(24.dp))

                        SocialLoginButtons(
                            onGuestClick = onGuestClick,
                            onRegisterClick = onRegisterClick,
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(Res.string.login_no_account),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
                TextButton(onClick = onRegisterClick) {
                    Text(
                        text = stringResource(Res.string.login_register_link),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginHeader() {
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
                    imageVector = Icons.Default.AccountTree,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(Res.string.login_title),
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = stringResource(Res.string.login_subtitle),
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun LoginForm(
    uiState: LoginUiState,
    onDataChange: (String, String) -> Unit,
    onForgotPasswordClick: () -> Unit,
) {
    val emptyFieldStr = stringResource(Res.string.error_empty_fields)
    val invalidEmailStr = stringResource(Res.string.error_invalid_email)
    val invalidPasswordStr = stringResource(Res.string.error_invalid_password)

    val emailErrorText =
        when (uiState.emailError) {
            LoginUiState.ValidationError.EMPTY -> emptyFieldStr
            LoginUiState.ValidationError.INVALID -> invalidEmailStr
            null -> null
        }

    val passwordErrorText =
        when (uiState.passwordError) {
            LoginUiState.ValidationError.EMPTY -> emptyFieldStr
            LoginUiState.ValidationError.INVALID -> invalidPasswordStr
            null -> null
        }

    Text(
        text = stringResource(Res.string.login_email_label),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    SoftTextField(
        value = uiState.email,
        onValueChange = { onDataChange(it, uiState.password) },
        placeholder = stringResource(Res.string.login_email_hint),
        leadingIcon = Icons.Default.Email,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        errorText = emailErrorText,
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(Res.string.login_password_label),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    var passwordVisible by remember { mutableStateOf(false) }
    SoftTextField(
        value = uiState.password,
        onValueChange = { onDataChange(uiState.email, it) },
        placeholder = stringResource(Res.string.login_password_hint),
        leadingIcon = Icons.Default.Lock,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
            }
        },
        errorText = passwordErrorText,
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        TextButton(onClick = onForgotPasswordClick) {
            Text(
                text = stringResource(Res.string.login_forgot_password),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun LoginButton(
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
            text = stringResource(Res.string.login_button),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun OrContinueWithDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        )
        Text(
            text = stringResource(Res.string.login_or_continue),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        )
    }
}

@Composable
private fun SocialLoginButtons(
    onGuestClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        CircularIconButton(
            icon = Icons.AutoMirrored.Filled.Login,
            contentDescription = stringResource(Res.string.login_guest_label),
            onClick = onGuestClick,
        )

        Spacer(modifier = Modifier.size(16.dp))

        CircularIconButton(
            icon = Icons.Default.PersonAdd,
            contentDescription = stringResource(Res.string.login_create_account_label),
            onClick = onRegisterClick,
        )
    }
}

@Composable
private fun CircularIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(60.dp),
        shape = CircleShape,
        color = SurfaceContainerHighest,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
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
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
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
        visualTransformation = visualTransformation,
        singleLine = true,
        trailingIcon = trailingIcon,
    )
}

@Composable
@Preview
fun LoginScreenPreview() {
    GenogramiaTheme {
        LoginContent(
            uiState = LoginUiState(),
            onDataChange = { _, _ -> },
            onLoginClick = {},
            onBackClick = {},
            onRegisterClick = {},
            onForgotPasswordClick = {},
            onGuestClick = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
