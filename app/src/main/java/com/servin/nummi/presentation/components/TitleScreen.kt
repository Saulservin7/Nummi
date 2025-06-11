package com.servin.nummi.presentation.components

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun TitleScreen(title: String) {

    Text(
        title,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 20.sp,
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
    )

}


@Preview
@Composable
fun TitleScreenPreview() {
    TitleScreen("Agregar transacción")
}