package dev.saragones3.genogramia.presentation.registration

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary
import dev.saragones3.genogramia.ui.theme.SurfaceContainerHighest
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.app_name
import genogramia.composeapp.generated.resources.error_empty_fields
import genogramia.composeapp.generated.resources.error_invalid_email
import genogramia.composeapp.generated.resources.error_invalid_password
import genogramia.composeapp.generated.resources.login_or_continue
import genogramia.composeapp.generated.resources.registration_already_have_account
import genogramia.composeapp.generated.resources.registration_apple
import genogramia.composeapp.generated.resources.registration_button
import genogramia.composeapp.generated.resources.registration_email_hint
import genogramia.composeapp.generated.resources.registration_email_label
import genogramia.composeapp.generated.resources.registration_google
import genogramia.composeapp.generated.resources.registration_login_link
import genogramia.composeapp.generated.resources.registration_name_hint
import genogramia.composeapp.generated.resources.registration_name_label
import genogramia.composeapp.generated.resources.registration_password_hint
import genogramia.composeapp.generated.resources.registration_password_label
import genogramia.composeapp.generated.resources.registration_subtitle
import genogramia.composeapp.generated.resources.registration_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun RegistrationScreen(
    state: RegistrationState,
    onEvent: (RegistrationEvent) -> Unit,
    onBackClick: () -> Unit,
    onRegistrationSuccess: () -> Unit,
) {
    LaunchedEffect(state.isRegistrationSuccess) {
        if (state.isRegistrationSuccess) {
            onRegistrationSuccess()
            onEvent(RegistrationEvent.OnRegistrationSuccessConsumed)
        }
    }

    RegistrationContent(
        state = state,
        onEvent = onEvent,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistrationContent(
    state: RegistrationState,
    onEvent: (RegistrationEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
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
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RegistrationHeader()

            Spacer(modifier = Modifier.height(32.dp))

            RegistrationForm(state = state, onEvent = onEvent)

            Spacer(modifier = Modifier.height(32.dp))

            OrContinueWithDivider()

            Spacer(modifier = Modifier.height(24.dp))

            RegistrationSocialSection(onEvent = onEvent)

            Spacer(modifier = Modifier.height(32.dp))

            RegistrationFooter(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RegistrationHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.AccountTree,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Primary,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Primary,
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(Res.string.registration_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.registration_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RegistrationForm(
    state: RegistrationState,
    onEvent: (RegistrationEvent) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Name Field
        RegistrationField(
            label = stringResource(Res.string.registration_name_label),
            value = state.name,
            onValueChange = {
                onEvent(RegistrationEvent.OnDataChanged(it, state.email, state.password))
            },
            placeholder = stringResource(Res.string.registration_name_hint),
            leadingIcon = Icons.Default.Person,
            error =
                if (state.nameError == RegistrationState.ValidationError.EMPTY) {
                    stringResource(Res.string.error_empty_fields)
                } else {
                    null
                },
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        RegistrationField(
            label = stringResource(Res.string.registration_email_label),
            value = state.email,
            onValueChange = {
                onEvent(RegistrationEvent.OnDataChanged(state.name, it, state.password))
            },
            placeholder = stringResource(Res.string.registration_email_hint),
            leadingIcon = Icons.Default.Email,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
            error =
                when (state.emailError) {
                    RegistrationState.ValidationError.EMPTY -> stringResource(Res.string.error_empty_fields)
                    RegistrationState.ValidationError.INVALID -> stringResource(Res.string.error_invalid_email)
                    else -> null
                },
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        var passwordVisible by remember { mutableStateOf(false) }
        RegistrationField(
            label = stringResource(Res.string.registration_password_label),
            value = state.password,
            onValueChange = {
                onEvent(RegistrationEvent.OnDataChanged(state.name, state.email, it))
            },
            placeholder = stringResource(Res.string.registration_password_hint),
            leadingIcon = Icons.Default.Lock,
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                }
            },
            visualTransformation =
                if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            error =
                when (state.passwordError) {
                    RegistrationState.ValidationError.EMPTY -> stringResource(Res.string.error_empty_fields)
                    RegistrationState.ValidationError.INVALID -> stringResource(Res.string.error_invalid_password)
                    else -> null
                },
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (state.generalError != null) {
            Text(
                text = stringResource(state.generalError),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Register Button
        Button(
            onClick = { onEvent(RegistrationEvent.OnSignUpClicked) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
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
                Text(
                    text = stringResource(Res.string.registration_button),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun RegistrationSocialSection(onEvent: (RegistrationEvent) -> Unit) {
    Column {
        // Social Login Buttons
        SocialButton(
            text = stringResource(Res.string.registration_google),
            onClick = { onEvent(RegistrationEvent.OnGoogleSignUpClicked) },
            icon = {
                // Google logo placeholder
                Text(
                    text = "G",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4285F4),
                )
            },
        )

        Spacer(modifier = Modifier.height(12.dp))

        SocialButton(
            text = stringResource(Res.string.registration_apple),
            onClick = { onEvent(RegistrationEvent.OnAppleSignUpClicked) },
            icon = {
                // Apple logo placeholder
                Icon(
                    imageVector = Icons.Default.AccountTree, // Placeholder
                    contentDescription = null,
                    tint = Color.Black,
                )
            },
        )
    }
}

@Composable
private fun RegistrationFooter(onBackClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.registration_already_have_account),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
        TextButton(onClick = onBackClick) {
            Text(
                text = stringResource(Res.string.registration_login_link),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Primary,
            )
        }
    }
}

@Composable
private fun RegistrationField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    error: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    var isFocused by remember { mutableStateOf(false) }
    val isError = error != null

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp),
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = if (isError) MaterialTheme.colorScheme.error else Color.Gray,
                )
            },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            singleLine = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
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
                                shape = RoundedCornerShape(12.dp),
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
            isError = isError,
            supportingText =
                if (isError) {
                    {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                } else {
                    null
                },
        )
    }
}

@Composable
private fun SocialButton(
    text: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.align(Alignment.CenterStart)) {
                icon()
            }
            Text(
                text = text,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )
        }
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
@Preview
private fun RegistrationScreenPreview() {
    GenogramiaTheme {
        RegistrationContent(
            state = RegistrationState(),
            onEvent = {},
            onBackClick = {},
        )
    }
}
