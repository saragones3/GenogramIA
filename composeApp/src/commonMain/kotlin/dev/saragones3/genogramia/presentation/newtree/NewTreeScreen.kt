package dev.saragones3.genogramia.presentation.newtree

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.presentation.components.AddDiseaseBottomSheet
import dev.saragones3.genogramia.presentation.components.BasicInfoErrors
import dev.saragones3.genogramia.presentation.components.BasicInfoSection
import dev.saragones3.genogramia.presentation.components.DateFieldState
import dev.saragones3.genogramia.presentation.components.DatePickerModal
import dev.saragones3.genogramia.presentation.components.HealthSymbolsSection
import dev.saragones3.genogramia.presentation.components.IdentitySection
import dev.saragones3.genogramia.presentation.components.MedicalConditionCard
import dev.saragones3.genogramia.presentation.components.MedicalConditionEmptyCard
import dev.saragones3.genogramia.presentation.components.MedicalHistorySection
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.date_format
import genogramia.composeapp.generated.resources.new_tree_button
import genogramia.composeapp.generated.resources.new_tree_footer
import genogramia.composeapp.generated.resources.new_tree_guest_notice
import genogramia.composeapp.generated.resources.new_tree_header_subtitle
import genogramia.composeapp.generated.resources.new_tree_header_title
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
    val dateFormat = stringResource(Res.string.date_format)

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

            BasicInfoSection(
                firstName = state.person.firstName,
                onFirstNameChange = { onEvent(NewTreeEvent.OnFirstNameChanged(it)) },
                lastName = state.person.lastName,
                onLastNameChange = { onEvent(NewTreeEvent.OnLastNameChanged(it)) },
                birthDateState =
                    DateFieldState(
                        value = state.person.birthDateText,
                        onClick = { onEvent(NewTreeEvent.OnShowBirthDatePicker(true)) },
                        onClear = { onEvent(NewTreeEvent.OnClearBirthDate) },
                    ),
                deathDateState =
                    DateFieldState(
                        value = state.person.deathDateText,
                        onClick = { onEvent(NewTreeEvent.OnShowDeathDatePicker(true)) },
                        onClear = { onEvent(NewTreeEvent.OnClearDeathDate) },
                    ),
                errors =
                    BasicInfoErrors(
                        firstName = state.firstNameError == NewTreeState.ValidationError.EMPTY,
                        lastName = state.lastNameError == NewTreeState.ValidationError.EMPTY,
                    ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            IdentitySection(
                biologicalSex = state.person.biologicalSex,
                onBiologicalSexChange = { onEvent(NewTreeEvent.OnBiologicalSexChanged(it)) },
                biologicalSexError = state.biologicalSexError == NewTreeState.ValidationError.EMPTY,
                sexualOrientation = state.person.sexualOrientation,
                onSexualOrientationChange = { onEvent(NewTreeEvent.OnSexualOrientationChanged(it)) },
                sexualOrientationError = state.sexualOrientationError == NewTreeState.ValidationError.EMPTY,
            )

            Spacer(modifier = Modifier.height(16.dp))

            HealthSymbolsSection(
                substanceAbuse = state.person.substanceAbuse,
                onSubstanceAbuseChange = { onEvent(NewTreeEvent.OnSubstanceAbuseChanged(it)) },
                hasMentalHealthProblem = state.person.hasMentalHealthProblem,
                onMentalHealthProblemChange = { onEvent(NewTreeEvent.OnMentalHealthProblemChanged(it)) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            MedicalHistorySection(
                onAddClick = { onEvent(NewTreeEvent.OnShowAddDiseaseSheet(true)) },
            ) {
                if (state.person.medicalHistory.isNotEmpty()) {
                    state.person.medicalHistory.forEach { condition ->
                        MedicalConditionCard(
                            title = condition.diseaseTitle,
                            subtitle = condition.chapterTitle,
                            date = condition.diagnosisDateText,
                            onRemoveClick = {
                                onEvent(NewTreeEvent.OnRemoveDiseaseFromHistory(condition.diseaseCode))
                            },
                        )
                    }
                } else {
                    MedicalConditionEmptyCard()
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            NewTreeButton(state, onEvent)

            Spacer(modifier = Modifier.height(12.dp))

            NewTreeFooter(state.isGuest)

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (state.showBirthDatePicker) {
            DatePickerModal(
                initialDate = state.person.birthDateMillis,
                onDateSelected = { onEvent(NewTreeEvent.OnBirthDateSelected(it, dateFormat)) },
                onDismiss = { onEvent(NewTreeEvent.OnShowBirthDatePicker(false)) },
            )
        }

        if (state.showDeathDatePicker) {
            DatePickerModal(
                initialDate = state.person.deathDateMillis,
                onDateSelected = { onEvent(NewTreeEvent.OnDeathDateSelected(it, dateFormat)) },
                onDismiss = { onEvent(NewTreeEvent.OnShowDeathDatePicker(false)) },
            )
        }

        if (state.showAddDiseaseSheet) {
            AddDiseaseBottomSheet(
                searchQuery = state.diseaseSearchQuery,
                searchResults = state.diseaseSearchResults,
                selectedDisease = state.selectedDisease,
                diagnosisDateText = state.diagnosisDateText,
                onSearchQueryChange = { onEvent(NewTreeEvent.OnDiseaseSearchQueryChanged(it)) },
                onDiseaseSelected = { onEvent(NewTreeEvent.OnDiseaseSelected(it)) },
                onDiagnosisDateClick = { onEvent(NewTreeEvent.OnShowDiagnosisDatePicker(true)) },
                onAddClick = {
                    onEvent(
                        NewTreeEvent.OnAddDiseaseToHistory(
                            it,
                            state.diagnosisDateMillis,
                            dateFormat,
                        ),
                    )
                },
                onDismiss = { onEvent(NewTreeEvent.OnShowAddDiseaseSheet(false)) },
            )
        }

        if (state.showDiagnosisDatePicker) {
            DatePickerModal(
                initialDate = state.diagnosisDateMillis,
                onDateSelected = { onEvent(NewTreeEvent.OnDiagnosisDateSelected(it, dateFormat)) },
                onDismiss = { onEvent(NewTreeEvent.OnShowDiagnosisDatePicker(false)) },
            )
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
private fun NewTreeFooter(isGuest: Boolean) {
    Text(
        text =
            if (isGuest) {
                stringResource(Res.string.new_tree_guest_notice)
            } else {
                stringResource(Res.string.new_tree_footer)
            },
        style = MaterialTheme.typography.labelSmall,
        color =
            if (isGuest) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            },
        fontWeight = if (isGuest) FontWeight.Bold else FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 48.dp),
    )
}

private class NewTreeStateProvider : PreviewParameterProvider<NewTreeState> {
    override val values =
        sequenceOf(
            NewTreeState(),
            NewTreeState(
                person =
                    NewTreeUi(
                        firstName = "María Elena",
                        lastName = "García López",
                        biologicalSex = Person.BiologicalSex.FEMALE,
                        sexualOrientation = Person.SexualOrientation.HETEROSEXUAL,
                        birthDateText = "15/05/1980",
                    ),
            ),
            NewTreeState(
                person =
                    NewTreeUi(
                        firstName = "",
                        lastName = "",
                    ),
                firstNameError = NewTreeState.ValidationError.EMPTY,
                lastNameError = NewTreeState.ValidationError.EMPTY,
                biologicalSexError = NewTreeState.ValidationError.EMPTY,
                sexualOrientationError = NewTreeState.ValidationError.EMPTY,
            ),
            NewTreeState(isLoading = true),
            NewTreeState(isGuest = true),
        )
}

@Preview
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
