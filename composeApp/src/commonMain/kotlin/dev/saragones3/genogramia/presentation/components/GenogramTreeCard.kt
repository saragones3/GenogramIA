package dev.saragones3.genogramia.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.auth_home_description
import genogramia.composeapp.generated.resources.background_tree
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun GenogramTreeCard(
    title: String,
    ancestorCount: Int,
    lastUpdated: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
) {
    Surface(
        onClick = onButtonClick,
        modifier =
            modifier
                .fillMaxWidth()
                .height(280.dp),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.primary,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(Res.drawable.background_tree),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            brush =
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                        ),
                                ),
                        ).padding(24.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (badgeText != null) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                        ) {
                            Text(
                                text = badgeText.uppercase(),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(Res.string.auth_home_description, ancestorCount, lastUpdated),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 2,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onButtonClick,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
                    ) {
                        Text(
                            text = buttonText,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun GenogramTreeCardPreview() {
    GenogramiaTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            GenogramTreeCard(
                title = "Smith Family",
                ancestorCount = 27,
                lastUpdated = "2 days ago",
                buttonText = "Open Archive",
                onButtonClick = {},
                badgeText = "Primary Lineage",
            )
        }
    }
}
