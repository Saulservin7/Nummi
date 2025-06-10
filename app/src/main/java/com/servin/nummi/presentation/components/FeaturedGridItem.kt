package com.servin.nummi.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.servin.nummi.R
import com.servin.nummi.presentation.home.HomeFeature

// Inspirado en como Pokedex crea un `PokemonItem` para su lista.
@Composable
fun FeatureGridItem(
    feature: HomeFeature,
    onItemClick: (String) -> Unit, // Callback para manejar el clic, pasando la ruta.
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable { onItemClick(feature.route) }, // Al hacer clic, invocamos el callback.
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .defaultMinSize(minHeight = 130.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = feature.iconRes),
                contentDescription = feature.title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                maxLines = 2
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FeaturedGridItemPreview() {
    FeatureGridItem(
        feature = HomeFeature(
            title = "Example Feature",
            iconRes = R.drawable.play_store_512, // Replace with a valid drawable resource ID
            route = "example_route"
        ),
        onItemClick = { },
        modifier = Modifier,
    )
}