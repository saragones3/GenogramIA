package dev.saragones3.genogramia.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary
import dev.saragones3.genogramia.ui.theme.SurfaceContainerHighest
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.medical_add_title
import genogramia.composeapp.generated.resources.medical_add_to_history
import genogramia.composeapp.generated.resources.medical_common
import genogramia.composeapp.generated.resources.medical_diagnosis_date
import genogramia.composeapp.generated.resources.medical_exact_matches
import genogramia.composeapp.generated.resources.medical_genetic
import genogramia.composeapp.generated.resources.medical_search_hint
import genogramia.composeapp.generated.resources.medical_select_date
import org.jetbrains.compose.resources.stringResource

@Suppress("LongParameterList")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDiseaseBottomSheet(
    searchQuery: String,
    searchResults: List<Disease>,
    selectedDisease: Disease?,
    diagnosisDateText: String,
    onSearchQueryChange: (String) -> Unit,
    onDiseaseSelected: (Disease) -> Unit,
    onDiagnosisDateClick: () -> Unit,
    onAddClick: (Disease) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier =
                    Modifier
                        .padding(top = 12.dp)
                        .size(32.dp, 4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)),
            )
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
        ) {
            Header(onDismiss)
            Spacer(modifier = Modifier.height(16.dp))
            SearchField(searchQuery, onSearchQueryChange)
            Spacer(modifier = Modifier.height(24.dp))
            if (searchQuery.isNotEmpty()) {
                SearchResultsSection(
                    results = searchResults,
                    selectedDisease = selectedDisease,
                    onDiseaseSelected = onDiseaseSelected,
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            DiagnosisDateSelector(onDiagnosisDateClick, diagnosisDateText)
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { selectedDisease?.let { onAddClick(it) } },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = selectedDisease != null,
            ) {
                Text(
                    text = stringResource(Res.string.medical_add_to_history),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun Header(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.medical_add_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null)
        }
    }
}

@Composable
private fun SearchField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = stringResource(Res.string.medical_search_hint),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        },
        shape = RoundedCornerShape(28.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceContainerHighest.copy(alpha = 0.5f),
                unfocusedContainerColor = SurfaceContainerHighest.copy(alpha = 0.5f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
        singleLine = true,
    )
}

@Composable
private fun SearchResultsSection(
    results: List<Disease>,
    selectedDisease: Disease?,
    onDiseaseSelected: (Disease) -> Unit,
) {
    LazyColumn(modifier = Modifier.height(300.dp)) {
        item {
            Text(
                text = stringResource(Res.string.medical_exact_matches),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(results) { disease ->
            DiseaseResultItem(
                disease = disease,
                isSelected = selectedDisease?.code == disease.code,
                onClick = { onDiseaseSelected(disease) },
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            )
        }
    }
}

@Composable
private fun DiseaseResultItem(
    disease: Disease,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            Primary.copy(alpha = 0.1f)
                        } else {
                            Primary.copy(alpha = 0.1f)
                        },
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Default.Check else Icons.Default.MedicalInformation,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = disease.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Primary else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "${disease.chapterTitle} • ${if (disease.isGenetic) {
                    stringResource(
                        Res.string.medical_genetic,
                    )
                } else {
                    stringResource(Res.string.medical_common)
                }}",
                style = MaterialTheme.typography.bodySmall,
                color =
                    if (isSelected) {
                        Primary.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.6f,
                        )
                    },
            )
        }
        Icon(
            imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Add,
            contentDescription = null,
            tint = if (isSelected) Primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        )
    }
}

@Composable
private fun DiagnosisDateSelector(
    onDiagnosisDateClick: () -> Unit,
    diagnosisDateText: String,
) {
    Text(
        text = stringResource(Res.string.medical_diagnosis_date),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerHighest.copy(alpha = 0.3f))
                .clickable { onDiagnosisDateClick() }
                .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text =
                    if (diagnosisDateText.isEmpty()) {
                        stringResource(
                            Res.string.medical_select_date,
                        )
                    } else {
                        diagnosisDateText
                    },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview
@Composable
private fun AddDiseaseBottomSheetPreview() {
    GenogramiaTheme {
        Box(modifier = Modifier.background(Color.White).padding(16.dp)) {
            AddDiseaseBottomSheet(
                searchQuery = "",
                searchResults = emptyList(),
                selectedDisease = null,
                diagnosisDateText = "",
                onSearchQueryChange = {},
                onDiseaseSelected = {},
                onDiagnosisDateClick = {},
                onAddClick = {},
                onDismiss = {},
            )
        }
    }
}
