package dev.saragones3.genogramia.presentation.addperson

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.saragones3.genogramia.presentation.components.BasicInfoErrors
import dev.saragones3.genogramia.presentation.components.BasicInfoSection
import dev.saragones3.genogramia.presentation.components.DatePickerModal
import dev.saragones3.genogramia.presentation.components.IdentitySection
import dev.saragones3.genogramia.presentation.components.MedicalHistorySection
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.add_person_save
import genogramia.composeapp.generated.resources.add_person_title
import genogramia.composeapp.generated.resources.date_format
import genogramia.composeapp.generated.resources.edit_person_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddPersonScreen(
    treeId: String,
    personId: String? = null,
    onBackClick: () -> Unit,
    onPersonAdded: () -> Unit,
) {
    val viewModel: AddPersonViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(treeId, personId) {
        viewModel.onEvent(AddPersonEvent.Initialize(treeId, personId))
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onPersonAdded()
            viewModel.onEvent(AddPersonEvent.OnResetState)
        }
    }

    AddPersonContent(
        treeId = treeId,
        state = state,
        onBackClick = onBackClick,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPersonContent(
    treeId: String,
    state: AddPersonState,
    onBackClick: () -> Unit,
    onEvent: (AddPersonEvent) -> Unit,
) {
    val isEditMode = state.personId != null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text =
                            if (isEditMode) {
                                stringResource(
                                    Res.string.edit_person_title,
                                )
                            } else {
                                stringResource(Res.string.add_person_title)
                            },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
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
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Primary,
                    ),
            )
        },
    ) { padding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                ) {
                    AddPersonForm(
                        state = state,
                        onEvent = onEvent,
                    )
                }

                StickySaveButton(
                    isLoading = state.isLoading,
                    onClick = { onEvent(AddPersonEvent.OnSaveClicked(treeId)) },
                )
            }

            if (state.showBirthDatePicker) {
                val dateFormat = stringResource(Res.string.date_format)
                DatePickerModal(
                    initialDate = state.person.birthDateMillis,
                    onDateSelected = { onEvent(AddPersonEvent.OnBirthDateSelected(it, dateFormat)) },
                    onDismiss = { onEvent(AddPersonEvent.OnShowBirthDatePicker(false)) },
                )
            }

            if (state.showDeathDatePicker) {
                val dateFormat = stringResource(Res.string.date_format)
                DatePickerModal(
                    initialDate = state.person.deathDateMillis,
                    onDateSelected = { onEvent(AddPersonEvent.OnDeathDateSelected(it, dateFormat)) },
                    onDismiss = { onEvent(AddPersonEvent.OnShowDeathDatePicker(false)) },
                )
            }
        }
    }
}

@Composable
private fun AddPersonForm(
    state: AddPersonState,
    onEvent: (AddPersonEvent) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicInfoSection(
            firstName = state.person.firstName,
            onFirstNameChange = { onEvent(AddPersonEvent.OnFirstNameChanged(it)) },
            lastName = state.person.lastName,
            onLastNameChange = { onEvent(AddPersonEvent.OnLastNameChanged(it)) },
            birthDate = state.person.birthDateText,
            onBirthDateClick = { onEvent(AddPersonEvent.OnShowBirthDatePicker(true)) },
            deathDate = state.person.deathDateText,
            onDeathDateClick = { onEvent(AddPersonEvent.OnShowDeathDatePicker(true)) },
            errors =
                BasicInfoErrors(
                    firstName = state.firstNameError != null,
                    lastName = state.lastNameError != null,
                    birthDate = state.birthDateError != null,
                ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        IdentitySection(
            biologicalSex = state.person.biologicalSex,
            onBiologicalSexChange = { onEvent(AddPersonEvent.OnBiologicalSexChanged(it)) },
            biologicalSexError = state.biologicalSexError != null,
            sexualOrientation = state.person.sexualOrientation,
            onSexualOrientationChange = { onEvent(AddPersonEvent.OnSexualOrientationChanged(it)) },
            sexualOrientationError = state.sexualOrientationError != null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MedicalHistorySection()
    }
}

@Composable
private fun StickySaveButton(
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
    ) {
        Button(
            onClick = onClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            enabled = !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = stringResource(Res.string.add_person_save),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Preview
@Composable
private fun AddPersonScreenPreview() {
    GenogramiaTheme {
        AddPersonContent(
            treeId = "123",
            state = AddPersonState(),
            onBackClick = {},
            onEvent = {},
        )
    }
}
