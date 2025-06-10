package com.servin.nummi.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.servin.nummi.R
import com.servin.nummi.presentation.auth.viewmodel.AuthViewModel
import com.servin.nummi.presentation.components.BalanceChart
import com.servin.nummi.presentation.components.FeatureGridItem
import com.servin.nummi.presentation.components.LegendLabelKey
import com.servin.nummi.presentation.components.charts.PreviewBox
import com.servin.nummi.presentation.navigation.AppScreens
import kotlinx.coroutines.runBlocking

@Composable
fun HomeScreen(
    onFeatureClick: (String) -> Unit = {},
) {
    
    Column(modifier = Modifier.fillMaxSize().padding(15.dp)) {


        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // <-- Este es el cambio clave. Forzamos la grilla a tener 3 columnas.

            modifier = Modifier.fillMaxWidth()
        ) {
            items(features) { feature ->
                FeatureGridItem(
                    feature = feature,
                    onItemClick = {
                        onFeatureClick(feature.route)
                    }
                )

            }
        }
        val modelProducer = remember { CartesianChartModelProducer() }
        val x = listOf(
            "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom",
            "Lun 2", "Mar 2", "Mié 2", "Jue 2"
        )

        val y = mapOf(
            "Ingresos" to listOf(1000.0, 2000.3, 4200.4, 7000.6, 6000.5, 2.3, 8000.2, 2.2, 2.2, 2.1, 2000.0),
            "Egresos" to listOf(2000, 4000.3, 0.4, 0.8, 4000.0, 2000.0, 2.6, 5000.8, 3.1, 3.3, 1200.6)
        )

        runBlocking {
            modelProducer.runTransaction {
                columnSeries {
                    y.values.forEach { series ->
                        series(series as List<Number>) // usa cast si y no está tipado explícitamente
                    }
                }
                extras { it[LegendLabelKey] = y.keys.toSet() }
            }
        }

        PreviewBox { BalanceChart(modelProducer) }
    } 
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(

    )
}