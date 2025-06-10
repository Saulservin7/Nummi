package com.servin.nummi.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.servin.nummi.domain.model.Transaction
import com.servin.nummi.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.launch

// Opt-in para las APIs experimentales de Material 3 que usaremos (TopAppBar y ModalBottomSheet).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Recolectamos el estado del ViewModel. 'collectAsStateWithLifecycle' es la forma segura
    // de hacerlo, ya que detiene la recolección cuando la app está en segundo plano.
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Estados para controlar el BottomSheet (el panel para añadir gastos).
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Scaffold es un layout base de Material Design que nos da una estructura estándar
    // con TopAppBar, contenido principal y FloatingActionButton.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Movimientos") },
                // Usamos los colores del tema para consistencia.
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showBottomSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Gasto")
            }
        }
    ) { paddingValues -> // El contenido de la pantalla irá aquí, con el padding correcto.

        // El contenido principal de la pantalla.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Manejo de los diferentes estados de la UI.
            when {
                // Estado de carga inicial.
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                // Estado de error.
                state.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                    }
                }
                // Estado de éxito, pero la lista está vacía.
                state.transactions.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aún no tienes movimientos.\n¡Añade tu primer gasto!", textAlign = TextAlign.Center)
                    }
                }
                // Estado de éxito con datos.
                else -> {
                    // LazyColumn es la versión de Compose para RecyclerView. Es eficiente para listas largas.
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Iteramos sobre la lista de transacciones del estado.
                        items(state.transactions) { transaction ->
                            TransactionItem(transaction = transaction)
                        }
                    }
                }
            }
        }

        // Si showBottomSheet es true, se mostrará el panel inferior.
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                // Contenido del panel: el formulario para añadir un gasto.
                AddExpenseForm(
                    onAddExpense = { amount, category, description ->
                        // Llamamos al método del ViewModel para añadir el gasto.
                        viewModel.addExpense(amount, category, description)
                        // Ocultamos el panel después de añadir el gasto.
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                )
            }
        }
    }
}

/**
 * Un Composable reutilizable para mostrar un único item de transacción.
 */
@Composable
fun TransactionItem(transaction: Transaction) {
    // Formateador de fecha para mostrarla de una manera legible.
    val formatter = remember { SimpleDateFormat("dd MMM, yyyy", Locale("es", "ES")) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = transaction.category, style = MaterialTheme.typography.titleMedium)
                // Mostramos la descripción solo si existe.
                AnimatedVisibility(visible = transaction.description?.isNotBlank() == true) {
                    Text(text = transaction.description!!, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("$%.2f", transaction.amount),
                    // Si es un gasto, el color es el de error (rojo). Si es ingreso, el primario.
                    color = if (transaction.type == TransactionType.EXPENSE) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = formatter.format(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}


/**
 * Composable que contiene el formulario para añadir un nuevo gasto.
 */
@Composable
fun AddExpenseForm(
    onAddExpense: (amount: Double, category: String, description: String?) -> Unit
) {
    // Estados locales para los campos del formulario.
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nuevo Gasto", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Campo para el monto.
        OutlinedTextField(
            value = amount,
            onValueChange = {
                // Permitimos solo números y un punto decimal.
                if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    amount = it
                }
            },
            label = { Text("Monto ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = hasError && amount.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        // Campo para la categoría.
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Categoría (ej. Comida)") },
            isError = hasError && category.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        // Campo para la descripción.
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción (Opcional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Botón para guardar.
        Button(
            onClick = {
                val amountDouble = amount.toDoubleOrNull()
                // Validamos que el monto y la categoría no estén vacíos.
                if (amountDouble != null && category.isNotBlank()) {
                    hasError = false
                    onAddExpense(amountDouble, category, description.ifBlank { null })
                } else {
                    hasError = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Gasto")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}