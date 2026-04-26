package dev.saragones3.genogramia.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val GenogramiaShapes = Shapes(
    small = RoundedCornerShape(4.dp), // Connecting Lines joints
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp) // Family Member Cards, Bottom Sheets
)

// Aliases based on DESIGN.md
val ShapeFull = CircleShape // Primary Action Buttons
