package com.servin.nummi.presentation.saving

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.servin.nummi.domain.model.SavingGoal
import com.servin.nummi.ui.theme.NummiTheme
import java.text.NumberFormat
import java.util.Locale

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

    val snackbarHostState = remember { SnackbarHostState() }

    // Observa el estado de `addGoalState` para mostrar Snackbars y luego resetear el estado.
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
                    onConfirm = { name, target, current ->
                        viewModel.addSavingGoal(name, target, current)
                        showDialog = false
                    },
                    addState = addGoalState
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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
                            text = "Aún no tienes metas de ahorro.\n¡Presiona el botón '+' para añadir una!",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        SavingGoalsList(goals = savingGoals)
                    }
                }
            }
        }
    }
}

@Composable
fun SavingGoalsList(goals: List<SavingGoal>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(goals) { goal ->
            SavingGoalItem(goal = goal, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun SavingGoalItem(goal: SavingGoal, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = goal.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
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
        }
    }
}

@Composable
fun AddSavingGoalDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (name: String, target: String, current: String) -> Unit,
    addState: AddSavingGoalState
) {
    var name by rememberSaveable { mutableStateOf("") }
    var targetAmount by rememberSaveable { mutableStateOf("") }
    var currentAmount by rememberSaveable { mutableStateOf("") }

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
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, targetAmount, currentAmount) },
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

// --- Previews ---

@Preview(showBackground = true)
@Composable
fun SavingGoalItemPreview() {
    val sampleGoal = SavingGoal(
        id = "1",
        name = "Viaje a la playa",
        targetAmount = 1000.0,
        currentAmount = 450.0
    )
    NummiTheme {
        SavingGoalItem(goal = sampleGoal)
    }
}

@Preview(showBackground = true)
@Composable
fun SavingGoalsListPreview() {
    val sampleGoals = listOf(
        SavingGoal(id = "1", name = "Viaje a la playa", targetAmount = 1000.0, currentAmount = 450.0),
        SavingGoal(id = "2", name = "Nuevo Teléfono", targetAmount = 800.0, currentAmount = 800.0),
        SavingGoal(id = "3", name = "Computadora Gamer", targetAmount = 2500.0, currentAmount = 500.0)
    )
    NummiTheme {
        SavingGoalsList(goals = sampleGoals)
    }
}

@Preview(showBackground = true)
@Composable
fun AddSavingGoalDialogPreview() {
    NummiTheme {
        AddSavingGoalDialog(
            onDismissRequest = {},
            onConfirm = { _, _, _ -> },
            addState = AddSavingGoalState.Idle
        )
    }
}