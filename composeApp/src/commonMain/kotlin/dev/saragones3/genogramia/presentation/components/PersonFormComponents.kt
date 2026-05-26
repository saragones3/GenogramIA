package dev.saragones3.genogramia.presentation.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Female
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
    val birthDate: Boolean = false,
)

@Composable
fun BasicInfoSection(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    birthDate: String,
    onBirthDateClick: () -> Unit,
    deathDate: String,
    onDeathDateClick: () -> Unit,
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
            value = birthDate,
            onValueChange = { },
            placeholder = stringResource(Res.string.new_tree_birth_date_hint),
            trailingIcon = Icons.Default.CalendarToday,
            isError = errors.birthDate,
            onClick = onBirthDateClick,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FormField(
            label = stringResource(Res.string.new_tree_death_date_label),
            value = deathDate,
            onValueChange = { },
            placeholder = stringResource(Res.string.new_tree_death_date_hint),
            trailingIcon = Icons.Default.CalendarToday,
            optionalLabel = stringResource(Res.string.new_tree_death_date_optional),
            onClick = onDeathDateClick,
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
fun MedicalHistorySection(content: @Composable (ColumnScope.() -> Unit)? = null) {
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
        if (content != null) {
            content()
        } else {
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
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    trailingIcon: ImageVector? = null,
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

        Box(modifier = Modifier.fillMaxWidth()) {
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
        }
    }
}

data class SelectorOption(
    val text: String,
    val icon: ImageVector? = null,
    val isSelected: Boolean,
    val onClick: () -> Unit,
)

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

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerHighest),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
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
                            fontWeight = if (option.isSelected) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.bodyMedium,
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
    val datePickerState = rememberDatePickerState(
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
                birthDate = "01/01/1990",
                onBirthDateClick = {},
                deathDate = "",
                onDeathDateClick = {},
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
private fun MedicalHistorySectionEmptyPreview() {
    GenogramiaTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
            MedicalHistorySection()
        }
    }
}

@Preview
@Composable
private fun MedicalHistorySectionWithContentPreview() {
    GenogramiaTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
            MedicalHistorySection {
                Text("Custom Medical Content")
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
