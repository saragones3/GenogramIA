package dev.saragones3.genogramia.presentation.newtree

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary
import dev.saragones3.genogramia.ui.theme.SurfaceContainerHighest
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.error_empty_fields
import genogramia.composeapp.generated.resources.new_tree_birth_date_hint
import genogramia.composeapp.generated.resources.new_tree_birth_date_label
import genogramia.composeapp.generated.resources.new_tree_button
import genogramia.composeapp.generated.resources.new_tree_death_date_hint
import genogramia.composeapp.generated.resources.new_tree_death_date_label
import genogramia.composeapp.generated.resources.new_tree_death_date_optional
import genogramia.composeapp.generated.resources.new_tree_first_name_hint
import genogramia.composeapp.generated.resources.new_tree_first_name_label
import genogramia.composeapp.generated.resources.new_tree_footer
import genogramia.composeapp.generated.resources.new_tree_header_subtitle
import genogramia.composeapp.generated.resources.new_tree_header_title
import genogramia.composeapp.generated.resources.new_tree_last_name_hint
import genogramia.composeapp.generated.resources.new_tree_last_name_label
import genogramia.composeapp.generated.resources.new_tree_medical_add
import genogramia.composeapp.generated.resources.new_tree_medical_desc
import genogramia.composeapp.generated.resources.new_tree_medical_empty
import genogramia.composeapp.generated.resources.new_tree_orientation_hetero
import genogramia.composeapp.generated.resources.new_tree_orientation_label
import genogramia.composeapp.generated.resources.new_tree_orientation_other
import genogramia.composeapp.generated.resources.new_tree_section_basic
import genogramia.composeapp.generated.resources.new_tree_section_identity
import genogramia.composeapp.generated.resources.new_tree_section_medical
import genogramia.composeapp.generated.resources.new_tree_sex_female
import genogramia.composeapp.generated.resources.new_tree_sex_label
import genogramia.composeapp.generated.resources.new_tree_sex_male
import genogramia.composeapp.generated.resources.new_tree_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewTreeScreen(
    onBackClick: () -> Unit,
    onTreeCreated: (String) -> Unit,
) {
    val viewModel: NewTreeViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(NewTreeEvent.OnResetState)
    }

    LaunchedEffect(state.navigationEvent) {
        state.navigationEvent?.let { treeId ->
            onTreeCreated(treeId)
            viewModel.onEvent(NewTreeEvent.OnNavigationConsumed)
            viewModel.onEvent(NewTreeEvent.OnResetState)
        }
    }

    NewTreeContent(
        state = state,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewTreeContent(
    state: NewTreeState,
    onEvent: (NewTreeEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = { NewTreeTopBar(onBackClick) },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NewTreeHeader()

            Spacer(modifier = Modifier.height(32.dp))

            BasicInfoSection(state, onEvent)

            Spacer(modifier = Modifier.height(16.dp))

            IdentitySection(state, onEvent)

            Spacer(modifier = Modifier.height(16.dp))

            MedicalHistorySection()

            Spacer(modifier = Modifier.height(48.dp))

            NewTreeButton(state, onEvent)

            Spacer(modifier = Modifier.height(12.dp))

            NewTreeFooter()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewTreeTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(Res.string.new_tree_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                )
            }
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
        actions = {
            Spacer(modifier = Modifier.width(48.dp))
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
    )
}

@Composable
private fun NewTreeHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier =
                Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.PersonAddAlt1,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.new_tree_header_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.new_tree_header_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp),
        )
    }
}

@Composable
private fun BasicInfoSection(
    state: NewTreeState,
    onEvent: (NewTreeEvent) -> Unit,
) {
    SectionCard(
        icon = Icons.Default.Badge,
        title = stringResource(Res.string.new_tree_section_basic),
    ) {
        NewTreeField(
            label = stringResource(Res.string.new_tree_first_name_label),
            value = state.person.firstName,
            onValueChange = { onEvent(NewTreeEvent.OnFirstNameChanged(it)) },
            placeholder = stringResource(Res.string.new_tree_first_name_hint),
            error =
                if (state.firstNameError == NewTreeState.ValidationError.EMPTY) {
                    stringResource(Res.string.error_empty_fields)
                } else {
                    null
                },
        )
        Spacer(modifier = Modifier.height(16.dp))
        NewTreeField(
            label = stringResource(Res.string.new_tree_last_name_label),
            value = state.person.lastName,
            onValueChange = { onEvent(NewTreeEvent.OnLastNameChanged(it)) },
            placeholder = stringResource(Res.string.new_tree_last_name_hint),
            error =
                if (state.lastNameError == NewTreeState.ValidationError.EMPTY) {
                    stringResource(Res.string.error_empty_fields)
                } else {
                    null
                },
        )
        Spacer(modifier = Modifier.height(16.dp))
        NewTreeField(
            label = stringResource(Res.string.new_tree_birth_date_label),
            value = state.person.birthDate.orEmpty(),
            onValueChange = { onEvent(NewTreeEvent.OnBirthDateChanged(it)) },
            placeholder = stringResource(Res.string.new_tree_birth_date_hint),
            trailingIcon = Icons.Default.CalendarToday,
            error =
                if (state.birthDateError == NewTreeState.ValidationError.EMPTY) {
                    stringResource(Res.string.error_empty_fields)
                } else {
                    null
                },
        )
        Spacer(modifier = Modifier.height(16.dp))
        NewTreeField(
            label = stringResource(Res.string.new_tree_death_date_label),
            value = state.person.deathDate.orEmpty(),
            onValueChange = { onEvent(NewTreeEvent.OnDeathDateChanged(it)) },
            placeholder = stringResource(Res.string.new_tree_death_date_hint),
            trailingIcon = Icons.Default.CalendarToday,
            optionalLabel = stringResource(Res.string.new_tree_death_date_optional),
        )
    }
}

@Composable
private fun IdentitySection(
    state: NewTreeState,
    onEvent: (NewTreeEvent) -> Unit,
) {
    SectionCard(
        icon = Icons.Default.Psychology,
        title = stringResource(Res.string.new_tree_section_identity),
    ) {
        OptionSelector(
            label = stringResource(Res.string.new_tree_sex_label),
            options =
                listOf(
                    SelectorOption(
                        text = stringResource(Res.string.new_tree_sex_male),
                        icon = Icons.Default.Male,
                        isSelected = state.person.biologicalSex == Person.BiologicalSex.MALE,
                        onClick = {
                            onEvent(NewTreeEvent.OnBiologicalSexChanged(Person.BiologicalSex.MALE))
                        },
                    ),
                    SelectorOption(
                        text = stringResource(Res.string.new_tree_sex_female),
                        icon = Icons.Default.Female,
                        isSelected = state.person.biologicalSex == Person.BiologicalSex.FEMALE,
                        onClick = {
                            onEvent(NewTreeEvent.OnBiologicalSexChanged(Person.BiologicalSex.FEMALE))
                        },
                    ),
                ),
            error =
                if (state.biologicalSexError == NewTreeState.ValidationError.EMPTY) {
                    stringResource(Res.string.error_empty_fields)
                } else {
                    null
                },
        )
        Spacer(modifier = Modifier.height(16.dp))
        OptionSelector(
            label = stringResource(Res.string.new_tree_orientation_label),
            options =
                listOf(
                    SelectorOption(
                        text = stringResource(Res.string.new_tree_orientation_hetero),
                        isSelected = state.person.sexualOrientation == Person.SexualOrientation.HETEROSEXUAL,
                        onClick = {
                            onEvent(NewTreeEvent.OnSexualOrientationChanged(Person.SexualOrientation.HETEROSEXUAL))
                        },
                    ),
                    SelectorOption(
                        text = stringResource(Res.string.new_tree_orientation_other),
                        isSelected = state.person.sexualOrientation == Person.SexualOrientation.OTHER,
                        onClick = {
                            onEvent(NewTreeEvent.OnSexualOrientationChanged(Person.SexualOrientation.OTHER))
                        },
                    ),
                ),
            error =
                if (state.sexualOrientationError == NewTreeState.ValidationError.EMPTY) {
                    stringResource(Res.string.error_empty_fields)
                } else {
                    null
                },
        )
    }
}

@Composable
private fun MedicalHistorySection() {
    SectionCard(
        icon = Icons.Default.MedicalInformation,
        title = stringResource(Res.string.new_tree_section_medical),
        action = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* TODO: US-017 */ },
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(Res.string.new_tree_medical_add),
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
    ) {
        Text(
            text = stringResource(Res.string.new_tree_medical_desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(32.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.new_tree_medical_empty),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun NewTreeButton(
    state: NewTreeState,
    onEvent: (NewTreeEvent) -> Unit,
) {
    Button(
        onClick = { onEvent(NewTreeEvent.OnCreateTreeClicked) },
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(Res.string.new_tree_button),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun NewTreeFooter() {
    Text(
        text = stringResource(Res.string.new_tree_footer),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 48.dp),
    )
}

@Composable
private fun SectionCard(
    icon: ImageVector,
    title: String,
    action: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                action?.invoke()
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier.padding(bottom = 20.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            )

            content()
        }
    }
}

@Composable
private fun NewTreeField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String? = null,
    trailingIcon: ImageVector? = null,
    optionalLabel: String? = null,
) {
    val isError = error != null

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 8.dp),
            )
            if (optionalLabel != null) {
                Text(
                    text = optionalLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
        }

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            leadingIcon =
                if (trailingIcon != null) {
                    {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                } else {
                    null
                },
            trailingIcon =
                if (trailingIcon != null) {
                    {
                        // This is just for visual, ideally it would open a picker
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                } else {
                    null
                },
            singleLine = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
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

data class SelectorOption(
    val text: String,
    val icon: ImageVector? = null,
    val isSelected: Boolean,
    val onClick: () -> Unit,
)

@Composable
private fun OptionSelector(
    label: String,
    options: List<SelectorOption>,
    error: String? = null,
) {
    val isError = error != null

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color =
                if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.6f,
                    )
                },
            modifier = Modifier.padding(bottom = 12.dp),
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerHighest),
            horizontalArrangement = Arrangement.spacedBy(1.dp), // For the divider effect
        ) {
            options.forEach { option ->
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (option.isSelected) Color.White else Color.Transparent)
                            .clickable(onClick = option.onClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (option.icon != null) {
                            Icon(
                                imageVector = option.icon,
                                contentDescription = null,
                                tint =
                                    if (option.isSelected) {
                                        Primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    },
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = option.text,
                            color =
                                if (option.isSelected) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                },
                            fontWeight =
                                if (option.isSelected) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }

        if (isError) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )
        }
    }
}

private class NewTreeStateProvider : PreviewParameterProvider<NewTreeState> {
    override val values =
        sequenceOf(
            NewTreeState(),
            NewTreeState(
                person =
                    Person().copy(
                        firstName = "María Elena",
                        lastName = "García López",
                        biologicalSex = Person.BiologicalSex.FEMALE,
                        sexualOrientation = Person.SexualOrientation.HETEROSEXUAL,
                        birthDate = "15/05/1980",
                    ),
            ),
            NewTreeState(
                person =
                    Person().copy(
                        firstName = "",
                        lastName = "",
                    ),
                firstNameError = NewTreeState.ValidationError.EMPTY,
                lastNameError = NewTreeState.ValidationError.EMPTY,
                birthDateError = NewTreeState.ValidationError.EMPTY,
                biologicalSexError = NewTreeState.ValidationError.EMPTY,
                sexualOrientationError = NewTreeState.ValidationError.EMPTY,
            ),
            NewTreeState(isLoading = true),
        )
}

@Preview(heightDp = 1500)
@Composable
private fun NewTreeScreenPreview(
    @PreviewParameter(NewTreeStateProvider::class) state: NewTreeState,
) {
    GenogramiaTheme {
        NewTreeContent(
            state = state,
            onEvent = {},
            onBackClick = {},
        )
    }
}
