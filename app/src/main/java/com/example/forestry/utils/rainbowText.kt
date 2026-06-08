package com.example.forestry.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

val brush = Brush.linearGradient(
    listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)
)
val rainbow = TextStyle(brush)