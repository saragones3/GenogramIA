package dev.saragones3.genogramia.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.saragones3.genogramia.ui.theme.Primary
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.delete_tree_confirm
import genogramia.composeapp.generated.resources.delete_tree_keep
import genogramia.composeapp.generated.resources.delete_tree_message
import genogramia.composeapp.generated.resources.delete_tree_title
import genogramia.composeapp.generated.resources.delete_tree_undone
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteTreeDialog(
    treeName: String,
    memberCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(64.dp)
                            .background(Color(0xFFFFEBEE), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(32.dp),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(Res.string.delete_tree_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(16.dp))

                val message = stringResource(Res.string.delete_tree_message, treeName, memberCount)

                Text(
                    text =
                        buildAnnotatedString {
                            val treeNameText = "'$treeName'"
                            val membersText = "$memberCount members"

                            // Note: This logic assumes the structure of the string for formatting.
                            // In a real multi-language app, we'd use more robust tag-based parsing
                            // if we needed precise styling on translated arguments.
                            val firstPart = message.indexOf(treeNameText)
                            val secondPart = message.indexOf(membersText)

                            if (firstPart != -1 && secondPart != -1) {
                                append(message.substring(0, firstPart))
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(treeNameText)
                                }
                                append(message.substring(firstPart + treeNameText.length, secondPart))
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(membersText)
                                }
                                append(message.substring(secondPart + membersText.length))
                            } else {
                                append(message)
                            }
                        },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(Res.string.delete_tree_undone),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFD32F2F),
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.delete_tree_keep),
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC62828),
                        ),
                ) {
                    Text(
                        text = stringResource(Res.string.delete_tree_confirm),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
