package com.servin.nummi.presentation.saving

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.servin.nummi.R // Asegúrate de tener un ícono de 'add' en tus recursos
import com.servin.nummi.domain.model.Budget // Import Budget for type hint
import com.servin.nummi.domain.model.Priority // Import Priority
import com.servin.nummi.domain.model.SavingGoal
import com.servin.nummi.ui.theme.NummiTheme
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.ceil // Import ceil

// Formateador de moneda para mostrar los montos de forma clara.
private fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(amount)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingGoalsScreen(
    viewModel: SavingGoalViewModel = hiltViewModel()
) {
    val savingGoals by viewModel.savingGoals.collectAsStateWithLifecycle()
    val addGoalState by viewModel.addGoalState.collectAsStateWithLifecycle()
    val loadGoalsState by viewModel.loadGoalsState.collectAsStateWithLifecycle()
    val currentBudget by viewModel.currentBudget.collectAsStateWithLifecycle()
    val biWeeklySalary by viewModel.biWeeklySalary.collectAsStateWithLifecycle()
    val totalSavingGoalsAmount by viewModel.totalSavingGoalsAmount.collectAsStateWithLifecycle() // Collect total

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(addGoalState) {
        when (val state = addGoalState) {
            is AddSavingGoalState.Success -> {
                snackbarHostState.showSnackbar(message = state.message)
                viewModel.resetAddGoalState()
            }
            is AddSavingGoalState.Error -> {
                snackbarHostState.showSnackbar(message = state.message)
                viewModel.resetAddGoalState()
            }
            else -> {
                // No hacer nada para Idle o Loading
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Metas de Ahorro") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            var showDialog by remember { mutableStateOf(false) }

            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_transactionhome), // Reemplaza con tu recurso
                    contentDescription = "Añadir Meta de Ahorro"
                )
            }

            if (showDialog) {
                AddSavingGoalDialog(
                    onDismissRequest = { showDialog = false },
                    onConfirm = { name, target, current, priority -> // Added priority
                        viewModel.addSavingGoal(name, target, current, priority) // Pass priority
                        showDialog = false
                    },
                    addState = addGoalState
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display Total Saving Goals Amount
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Suma Total de Metas de Ahorro:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = formatCurrency(totalSavingGoalsAmount),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary // Or another distinct color
                    )
                }
            }

            // Display Bi-Weekly Salary
            biWeeklySalary?.let { salaryAmount ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Salario Quincenal Estimado:",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = formatCurrency(salaryAmount),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Display Current Budget
            currentBudget?.let { budget ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Presupuesto Actual:",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = formatCurrency(budget.currentBudget),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (loadGoalsState) {
                    is LoadSavingGoalsState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is LoadSavingGoalsState.Error -> {
                        Text(
                            text = (loadGoalsState as LoadSavingGoalsState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    is LoadSavingGoalsState.Success -> {
                        if (savingGoals.isEmpty()) {
                            Text(
                                text = "Aún no tienes metas de ahorro.¡Presiona el botón '+' para añadir una!",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            SavingGoalsList(
                                goals = savingGoals,
                                currentBudget = currentBudget,
                                biWeeklySalary = biWeeklySalary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavingGoalsList(
    goals: List<SavingGoal>,
    currentBudget: Budget?,
    biWeeklySalary: Double?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(goals, key = { it.id }) { goal ->
            SavingGoalItem(
                goal = goal,
                currentBudget = currentBudget,
                biWeeklySalary = biWeeklySalary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun SavingGoalItem(
    goal: SavingGoal,
    currentBudget: Budget?,
    biWeeklySalary: Double?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = goal.priority.name, // Display priority
                    style = MaterialTheme.typography.labelSmall,
                    color = when (goal.priority) {
                        Priority.HIGH -> MaterialTheme.colorScheme.error
                        Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
                        Priority.LOW -> MaterialTheme.colorScheme.secondary
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            val progress = if (goal.targetAmount > 0) {
                (goal.currentAmount / goal.targetAmount).toFloat()
            } else {
                0f
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatCurrency(goal.currentAmount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formatCurrency(goal.targetAmount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // Spacer before the new message

            // Affordability Message
            val remainingAmount = goal.targetAmount - goal.currentAmount
            val affordabilityMessage = when {
                currentBudget != null && remainingAmount <= currentBudget.currentBudget -> {
                    "Se puede satisfacer con el presupuesto actual."
                }
                biWeeklySalary != null && biWeeklySalary > 0 && remainingAmount > 0 -> {
                    val periods = ceil(remainingAmount / biWeeklySalary).toInt()
                    "Hasta dentro de $periods quincena(s)."
                }
                else -> {
                    "El cronograma depende del salario quincenal."
                }
            }
            Text(
                text = affordabilityMessage,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AddSavingGoalDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (name: String, target: String, current: String, priority: Priority) -> Unit, // Added priority
    addState: AddSavingGoalState
) {
    var name by rememberSaveable { mutableStateOf("") }
    var targetAmount by rememberSaveable { mutableStateOf("") }
    var currentAmount by rememberSaveable { mutableStateOf("") }
    var selectedPriority by rememberSaveable { mutableStateOf(Priority.MEDIUM) } // State for priority

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Nueva Meta de Ahorro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la meta") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it },
                    label = { Text("Monto Objetivo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text("$") }
                )
                OutlinedTextField(
                    value = currentAmount,
                    onValueChange = { currentAmount = it },
                    label = { Text("Monto Actual (Opcional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text("$") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Prioridad:", style = MaterialTheme.typography.labelLarge)
                PrioritySelector( // Assuming PrioritySelector is defined elsewhere or will be added
                    selectedPriority = selectedPriority,
                    onPrioritySelected = { selectedPriority = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, targetAmount, currentAmount, selectedPriority) }, // Pass priority
                enabled = addState !is AddSavingGoalState.Loading
            ) {
                if (addState is AddSavingGoalState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.width(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}

// Dummy PrioritySelector - Replace with your actual implementation
@Composable
fun PrioritySelector(selectedPriority: Priority, onPrioritySelected: (Priority) -> Unit) {
    Row {
        Priority.values().forEach { priority ->
            OutlinedButton(
                onClick = { onPrioritySelected(priority) },
                modifier = Modifier.padding(end = 8.dp),
                border = BorderStroke(1.dp, if (selectedPriority == priority) MaterialTheme.colorScheme.primary else Color.Gray)
            ) {
                Text(priority.name)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavingGoalsScreenPreview() {
    NummiTheme {
        // You'll need to mock a SavingGoalViewModel for this preview to be meaningful
        // For now, it will use a default hiltViewModel() which might not work well in previews
        // without proper Hilt setup for previews.
        SavingGoalsScreen()
    }
}
