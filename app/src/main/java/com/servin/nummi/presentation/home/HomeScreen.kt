package com.servin.nummi.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.servin.nummi.presentation.components.BalanceChart
import com.servin.nummi.presentation.components.FeatureGridItem
import com.servin.nummi.presentation.components.LegendLabelKey
import com.servin.nummi.presentation.components.charts.PreviewBox
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.util.Locale

// Formatter for currency
private fun formatCurrency(amount: Double, currencyCode: String = "MXN"): String {
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
    // Potentially customize format based on currencyCode if needed, for now, it uses locale's default currency
    return format.format(amount)
}

@Composable
fun HomeScreen(
    onFeatureClick: (String) -> Unit = {},
    homeViewModel: HomeViewModel = hiltViewModel() // Inject ViewModel
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    // Main content column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), // Increased padding
        horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
    ) {

        // Display Salary if registered
        uiState.currentSalary?.let { salary ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp), // Adjusted padding
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Salario Mensual Registrado:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = formatCurrency(salary.monthlySalary, salary.currency),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Display Budget if registered
        uiState.currentBudget?.let { budget ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // Space below budget card
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Presupuesto Actual Registrado:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = formatCurrency(budget.currentBudget), // Assuming budget.currentBudget exists
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Optionally, display budget.lastUpdated or other relevant info
                }
            }
        }

        // Feature Grid (existing code)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp), // Add spacing between items
            verticalArrangement = Arrangement.spacedBy(16.dp)   // Add spacing between rows
        ) {
            items(features) { feature -> // Make sure 'features' is defined or passed to HomeScreen
                FeatureGridItem(
                    feature = feature,
                    onItemClick = {
                        onFeatureClick(feature.route)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Add some space

        // Chart (existing code)
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
                        series(series as List<Number>)
                    }
                }
                extras { it[LegendLabelKey] = y.keys.toSet() }
            }
        }
        PreviewBox { BalanceChart(modelProducer) }
    }

    // Show salary input dialog if needed
    if (uiState.showSalaryInputDialog) {
        SalaryInputDialog(
            onConfirm = { amountString ->
                amountString.toDoubleOrNull()?.let { amount ->
                    homeViewModel.saveSalary(amount)
                }
            },
            onDismiss = { homeViewModel.onSalaryInputDialogDismiss() }
        )
    }

    // Show budget input dialog if needed
    if (uiState.showBudgetInputDialog) {
        BudgetInputDialog(
            onConfirm = { amountString ->
                amountString.toDoubleOrNull()?.let { amount ->
                    homeViewModel.saveBudget(amount)
                }
            },
            onDismiss = { homeViewModel.onBudgetInputDialogDismiss() }
        )
    }
}

@Composable
fun SalaryInputDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var salaryInput by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Salario Mensual") },
        text = {
            OutlinedTextField(
                value = salaryInput,
                onValueChange = { salaryInput = it },
                label = { Text("Monto del salario") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("$") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (salaryInput.isNotBlank()) {
                        onConfirm(salaryInput)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun BudgetInputDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var budgetInput by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Presupuesto Actual") },
        text = {
            OutlinedTextField(
                value = budgetInput,
                onValueChange = { budgetInput = it },
                label = { Text("Monto del presupuesto") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("$") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (budgetInput.isNotBlank()) {
                        onConfirm(budgetInput)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // You might want to mock HomeViewModel and HomeState for a meaningful preview
    MaterialTheme { // Wrap with your app's theme for Preview
        HomeScreen()
    }
}
// Make sure 'features' is defined. Example:
// val features = listOf(
//     Feature("Feature 1", Icons.Filled.Favorite, "route1"),
//     Feature("Feature 2", Icons.Filled.Star, "route2"),
//     // ... other features
// )
// data class Feature(val name: String, val icon: ImageVector, val route: String)
// You'll need to define Feature data class and features list according to your project structure.
// If FeatureGridItem and features are defined in another file, ensure they are imported.
