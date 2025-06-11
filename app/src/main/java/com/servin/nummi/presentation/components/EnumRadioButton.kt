package com.servin.nummi.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EnumRadioButton(
    title: String, isSelected: Boolean, onOptionSelected: () -> Unit, modifier: Modifier
) {

    Row(
        modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected, onClick = onOptionSelected
            )
    ) {
        RadioButton(
            selected = isSelected, onClick = null
        )
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 5.dp)
        )
    }

}


@Preview(showBackground = true)
@Composable
fun EnumRadioButtonPreview() {
    EnumRadioButton(
        title = "Ingreso", isSelected = true, onOptionSelected = {}, modifier = Modifier
    )
}

