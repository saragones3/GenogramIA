package dev.saragones3.genogramia.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary
import dev.saragones3.genogramia.ui.theme.SurfaceContainerHighest
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.error_empty_fields
import genogramia.composeapp.generated.resources.health_mental_problem_grave
import genogramia.composeapp.generated.resources.health_mental_problem_label
import genogramia.composeapp.generated.resources.health_mental_problem_option_diagnosed
import genogramia.composeapp.generated.resources.health_substance_abuse_confirmed
import genogramia.composeapp.generated.resources.health_substance_abuse_label
import genogramia.composeapp.generated.resources.health_substance_abuse_none
import genogramia.composeapp.generated.resources.health_substance_abuse_recovery
import genogramia.composeapp.generated.resources.health_substance_abuse_suspected
import genogramia.composeapp.generated.resources.new_tree_birth_date_hint
import genogramia.composeapp.generated.resources.new_tree_birth_date_label
import genogramia.composeapp.generated.resources.new_tree_death_date_hint
import genogramia.composeapp.generated.resources.new_tree_death_date_label
import genogramia.composeapp.generated.resources.new_tree_death_date_optional
import genogramia.composeapp.generated.resources.new_tree_first_name_hint
import genogramia.composeapp.generated.resources.new_tree_first_name_label
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
import genogramia.composeapp.generated.resources.settings_cancel
import genogramia.composeapp.generated.resources.settings_confirm
import org.jetbrains.compose.resources.stringResource

data class BasicInfoErrors(
    val firstName: Boolean = false,
    val lastName: Boolean = false,
)

data class DateFieldState(
    val value: String,
    val onClick: () -> Unit,
    val onClear: () -> Unit,
)

@Composable
fun BasicInfoSection(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    birthDateState: DateFieldState,
    deathDateState: DateFieldState,
    errors: BasicInfoErrors = BasicInfoErrors(),
) {
    SectionCard(
        icon = Icons.Default.Badge,
        title = stringResource(Res.string.new_tree_section_basic),
    ) {
        FormField(
            label = stringResource(Res.string.new_tree_first_name_label),
            value = firstName,
            onValueChange = onFirstNameChange,
            placeholder = stringResource(Res.string.new_tree_first_name_hint),
            isError = errors.firstName,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FormField(
            label = stringResource(Res.string.new_tree_last_name_label),
            value = lastName,
            onValueChange = onLastNameChange,
            placeholder = stringResource(Res.string.new_tree_last_name_hint),
            isError = errors.lastName,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FormField(
            label = stringResource(Res.string.new_tree_birth_date_label),
            value = birthDateState.value,
            onValueChange = { },
            placeholder = stringResource(Res.string.new_tree_birth_date_hint),
            trailingIcon =
                if (birthDateState.value.isEmpty()) {
                    Icons.Default.CalendarToday
                } else {
                    Icons.Default.Close
                },
            onTrailingIconClick =
                if (birthDateState.value.isEmpty()) {
                    birthDateState.onClick
                } else {
                    birthDateState.onClear
                },
            optionalLabel = stringResource(Res.string.new_tree_death_date_optional),
            onClick = birthDateState.onClick,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FormField(
            label = stringResource(Res.string.new_tree_death_date_label),
            value = deathDateState.value,
            onValueChange = { },
            placeholder = stringResource(Res.string.new_tree_death_date_hint),
            trailingIcon =
                if (deathDateState.value.isEmpty()) {
                    Icons.Default.CalendarToday
                } else {
                    Icons.Default.Close
                },
            onTrailingIconClick =
                if (deathDateState.value.isEmpty()) {
                    deathDateState.onClick
                } else {
                    deathDateState.onClear
                },
            optionalLabel = stringResource(Res.string.new_tree_death_date_optional),
            onClick = deathDateState.onClick,
        )
    }
}

@Composable
fun IdentitySection(
    biologicalSex: Person.BiologicalSex,
    onBiologicalSexChange: (Person.BiologicalSex) -> Unit,
    biologicalSexError: Boolean,
    sexualOrientation: Person.SexualOrientation,
    onSexualOrientationChange: (Person.SexualOrientation) -> Unit,
    sexualOrientationError: Boolean,
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
                        isSelected = biologicalSex == Person.BiologicalSex.MALE,
                        onClick = { onBiologicalSexChange(Person.BiologicalSex.MALE) },
                    ),
                    SelectorOption(
                        text = stringResource(Res.string.new_tree_sex_female),
                        icon = Icons.Default.Female,
                        isSelected = biologicalSex == Person.BiologicalSex.FEMALE,
                        onClick = { onBiologicalSexChange(Person.BiologicalSex.FEMALE) },
                    ),
                ),
            isError = biologicalSexError,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OptionSelector(
            label = stringResource(Res.string.new_tree_orientation_label),
            options =
                listOf(
                    SelectorOption(
                        text = stringResource(Res.string.new_tree_orientation_hetero),
                        isSelected = sexualOrientation == Person.SexualOrientation.HETEROSEXUAL,
                        onClick = { onSexualOrientationChange(Person.SexualOrientation.HETEROSEXUAL) },
                    ),
                    SelectorOption(
                        text = stringResource(Res.string.new_tree_orientation_other),
                        isSelected = sexualOrientation == Person.SexualOrientation.OTHER,
                        onClick = { onSexualOrientationChange(Person.SexualOrientation.OTHER) },
                    ),
                ),
            isError = sexualOrientationError,
        )
    }
}

@Composable
fun HealthSymbolsSection(
    substanceAbuse: Person.SubstanceAbuse,
    onSubstanceAbuseChange: (Person.SubstanceAbuse) -> Unit,
    hasMentalHealthProblem: Boolean,
    onMentalHealthProblemChange: (Boolean) -> Unit,
) {
    SectionCard(
        icon = Icons.Default.Healing,
        title = stringResource(Res.string.health_mental_problem_label),
    ) {
        OptionSelector(
            label = stringResource(Res.string.health_substance_abuse_label),
            options =
                listOf(
                    SelectorOption(
                        text = stringResource(Res.string.health_substance_abuse_none),
                        isSelected = substanceAbuse == Person.SubstanceAbuse.NONE,
                        onClick = { onSubstanceAbuseChange(Person.SubstanceAbuse.NONE) },
                    ),
                    SelectorOption(
                        text = stringResource(Res.string.health_substance_abuse_confirmed),
                        isSelected = substanceAbuse == Person.SubstanceAbuse.CONFIRMED,
                        onClick = { onSubstanceAbuseChange(Person.SubstanceAbuse.CONFIRMED) },
                    ),
                    SelectorOption(
                        text = stringResource(Res.string.health_substance_abuse_suspected),
                        isSelected = substanceAbuse == Person.SubstanceAbuse.SUSPECTED,
                        onClick = { onSubstanceAbuseChange(Person.SubstanceAbuse.SUSPECTED) },
                    ),
                    SelectorOption(
                        text = stringResource(Res.string.health_substance_abuse_recovery),
                        isSelected = substanceAbuse == Person.SubstanceAbuse.RECOVERY,
                        onClick = { onSubstanceAbuseChange(Person.SubstanceAbuse.RECOVERY) },
                    ),
                ),
        )
        Spacer(modifier = Modifier.height(16.dp))
        OptionSelector(
            label = stringResource(Res.string.health_mental_problem_grave),
            options =
                listOf(
                    SelectorOption(
                        text = stringResource(Res.string.health_substance_abuse_none),
                        isSelected = !hasMentalHealthProblem,
                        onClick = { onMentalHealthProblemChange(false) },
                    ),
                    SelectorOption(
                        text = stringResource(Res.string.health_mental_problem_option_diagnosed),
                        isSelected = hasMentalHealthProblem,
                        onClick = { onMentalHealthProblemChange(true) },
                    ),
                ),
        )
    }
}

@Composable
fun MedicalHistorySection(
    onAddClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    SectionCard(
        icon = Icons.Default.MedicalInformation,
        title = stringResource(Res.string.new_tree_section_medical),
        action = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onAddClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
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
        content = content,
    )
}

@Composable
fun SectionCard(
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
fun MedicalConditionEmptyCard() {
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

@Composable
fun MedicalConditionCard(
    title: String,
    subtitle: String,
    date: String,
    onRemoveClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalInformation,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
                if (date.isNotEmpty()) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    optionalLabel: String? = null,
    onClick: (() -> Unit)? = null,
) {
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

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
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
                trailingIcon =
                    if (trailingIcon != null) {
                        {
                            Spacer(modifier = Modifier.size(24.dp))
                        }
                    } else {
                        null
                    },
                singleLine = true,
                readOnly = onClick != null,
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
                                text = stringResource(Res.string.error_empty_fields),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    } else {
                        null
                    },
            )

            if (onClick != null) {
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .clickable(onClick = onClick),
                )
            }

            if (trailingIcon != null) {
                if (trailingIcon == Icons.Default.Close) {
                    IconButton(
                        onClick = { onTrailingIconClick?.invoke() },
                        modifier = Modifier.padding(end = 12.dp).size(24.dp),
                    ) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                } else {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(end = 16.dp).size(18.dp),
                    )
                }
            }
        }
    }
}

data class SelectorOption(
    val text: String,
    val icon: ImageVector? = null,
    val isSelected: Boolean,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OptionSelector(
    label: String,
    options: List<SelectorOption>,
    isError: Boolean = false,
) {
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

        FlowRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerHighest)
                    .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            options.forEach { option ->
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (option.isSelected) Color.White else Color.Transparent)
                            .clickable(onClick = option.onClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    ) {
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
                            fontWeight = if (option.isSelected) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
        if (isError) {
            Text(
                text = stringResource(Res.string.error_empty_fields),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    initialDate: Long? = null,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = initialDate,
            yearRange = IntRange(1500, 3000),
        )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(it)
                    }
                    onDismiss()
                },
            ) {
                Text(text = stringResource(Res.string.settings_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.settings_cancel))
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview
@Composable
private fun BasicInfoSectionPreview() {
    GenogramiaTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
            BasicInfoSection(
                firstName = "Juan",
                onFirstNameChange = {},
                lastName = "Pérez",
                onLastNameChange = {},
                birthDateState =
                    DateFieldState(
                        value = "01/01/1990",
                        onClick = {},
                        onClear = {},
                    ),
                deathDateState =
                    DateFieldState(
                        value = "",
                        onClick = {},
                        onClear = {},
                    ),
                errors = BasicInfoErrors(),
            )
        }
    }
}

@Preview
@Composable
private fun IdentitySectionPreview() {
    GenogramiaTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
            IdentitySection(
                biologicalSex = Person.BiologicalSex.MALE,
                onBiologicalSexChange = {},
                biologicalSexError = false,
                sexualOrientation = Person.SexualOrientation.HETEROSEXUAL,
                onSexualOrientationChange = {},
                sexualOrientationError = false,
            )
        }
    }
}

@Preview
@Composable
private fun HealthSymbolsSectionPreview() {
    GenogramiaTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
            HealthSymbolsSection(
                substanceAbuse = Person.SubstanceAbuse.CONFIRMED,
                onSubstanceAbuseChange = {},
                hasMentalHealthProblem = true,
                onMentalHealthProblemChange = {},
            )
        }
    }
}

@Preview
@Composable
private fun MedicalHistorySectionEmptyPreview() {
    GenogramiaTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
            MedicalHistorySection(onAddClick = {}) {
                MedicalConditionEmptyCard()
            }
        }
    }
}

@Preview
@Composable
private fun MedicalHistorySectionWithContentPreview() {
    GenogramiaTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
            MedicalHistorySection(onAddClick = {}) {
                MedicalConditionCard(
                    title = "Hipertensión",
                    subtitle = "Sistema cardiovascular",
                    date = "15/05/2024",
                    onRemoveClick = {},
                )
                MedicalConditionCard(
                    title = "Asma",
                    subtitle = "Sistema respiratorio",
                    date = "02/10/2026",
                    onRemoveClick = {},
                )
            }
        }
    }
}

@Preview
@Composable
private fun FormFieldPreview() {
    GenogramiaTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
            FormField(
                label = "Label",
                value = "Value",
                onValueChange = {},
                placeholder = "Placeholder",
            )
        }
    }
}

@Preview
@Composable
private fun FormFieldErrorPreview() {
    GenogramiaTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
            FormField(
                label = "Label",
                value = "",
                onValueChange = {},
                placeholder = "Placeholder",
                isError = true,
            )
        }
    }
}

@Preview
@Composable
private fun OptionSelectorPreview() {
    GenogramiaTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
            OptionSelector(
                label = "Option Selector",
                options =
                    listOf(
                        SelectorOption(text = "Option 1", isSelected = true, onClick = {}),
                        SelectorOption(text = "Option 2", isSelected = false, onClick = {}),
                    ),
            )
        }
    }
}
