package dev.saragones3.genogramia.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import dev.saragones3.genogramia.ui.theme.Primary

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth().height(56.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(24.dp),
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.Gray,
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3F3F3),
                unfocusedContainerColor = Color(0xFFF3F3F3),
                disabledContainerColor = Color(0xFFF3F3F3),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        singleLine = true,
    )
}

@Preview
@Composable
private fun SearchBarPreview() {
    GenogramiaTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SearchBar(
                query = "",
                onQueryChange = {},
                placeholder = "Search tree...",
            )
        }
    }
}
