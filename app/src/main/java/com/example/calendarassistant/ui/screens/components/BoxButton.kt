package com.example.calendarassistant.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.calendarassistant.ui.theme.ButtonBlue
import com.example.calendarassistant.ui.theme.TextWhite

@Composable
fun BoxButton(padding: Dp, color: Color, onClick: () -> Unit, buttonText: String) {
    Box(
        modifier = Modifier
            .padding(padding)
            .background(ButtonBlue, shape = RoundedCornerShape(padding))
            .clickable { onClick() }
            .padding(horizontal = padding * 2, vertical = padding)
    ) {
        Text(text = buttonText, color = TextWhite)
    }
}