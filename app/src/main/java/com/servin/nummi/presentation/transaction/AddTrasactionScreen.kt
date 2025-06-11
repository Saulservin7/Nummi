package com.servin.nummi.presentation.transaction

    import android.widget.Toast
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.text.KeyboardOptions
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material3.Button
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.DropdownMenuItem
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.ExposedDropdownMenuBox
    import androidx.compose.material3.ExposedDropdownMenuDefaults
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.OutlinedTextField
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.SnackbarHost
    import androidx.compose.material3.SnackbarHostState
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.text.input.KeyboardType
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.hilt.navigation.compose.hiltViewModel
    import com.servin.nummi.domain.model.CategoryType
    import com.servin.nummi.domain.model.TransactionType
    import com.servin.nummi.presentation.components.EnumRadioButton
    import com.servin.nummi.presentation.components.TitleScreen
    import kotlinx.coroutines.launch

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddTransactionScreen(
        viewModel: TransactionViewModel = hiltViewModel(),
        onTransactionAdded: () -> Unit = {} // Callback para navegar o limpiar
    ) {
        val state by viewModel.uiState.collectAsState()
        val scrollState = rememberScrollState()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        // Efecto para mostrar Snackbar en caso de éxito o error
        LaunchedEffect(state.transactionAdded, state.error) {
            if (state.transactionAdded) {
                scope.launch {
                    snackbarHostState.showSnackbar("Transacción agregada exitosamente")
                }
                viewModel.resetTransactionAddedState() // Resetea el estado para evitar múltiples SnackBar
                onTransactionAdded() // Llama al callback, por ejemplo para navegar
            }
            state.error?.let { errorMessage ->
                scope.launch {
                    snackbarHostState.showSnackbar("Error: $errorMessage")
                }
                viewModel.clearError() // Limpia el error después de mostrarlo
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 20.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val transactionTypes = TransactionType.entries.toTypedArray()
                    val categoryTypes = CategoryType.entries.toTypedArray()

                    TitleScreen("Agregar Transacción")

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        "Selecciona tipo de transacción",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        transactionTypes.forEach { type ->
                            EnumRadioButton(
                                title = type.type,
                                isSelected = state.type == type,
                                onOptionSelected = { viewModel.onTypeChange(type) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Ingresa la Cantidad",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.amount,
                        onValueChange = { viewModel.onAmountChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("0.00") },
                        label = { Text("Cantidad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = state.error?.contains("Cantidad inválida") == true // Ejemplo de error específico
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Selecciona la categoría",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = state.isExpanded,
                        onExpandedChange = { viewModel.onDropdownExpandedChange(it) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.category.type,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.isExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = state.isExpanded,
                            onDismissRequest = { viewModel.onDropdownExpandedChange(false) },
                        ) {
                            categoryTypes.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.type) },
                                    onClick = {
                                        viewModel.onCategoryChange(category)
                                        viewModel.onDropdownExpandedChange(false)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Ingresa una descripción (opcional)",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { viewModel.onDescriptionChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej: Compra de supermercado") },
                        label = { Text("Descripción") },
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { viewModel.addTransactionFromState() },
                        enabled = !state.isLoading, // Deshabilita el botón mientras carga
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp, bottom = 16.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(24.dp), // Ajusta el tamaño si es necesario
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Registrar Transacción")
                        }
                    }
                }

                // Overlay de carga (opcional, si prefieres un bloqueo de pantalla completa)
                // if (state.isLoading) {
                //     Box(
                //         contentAlignment = Alignment.Center,
                //         modifier = Modifier
                //             .fillMaxSize()
                //             .background(Color.Black.copy(alpha = 0.3f)) // Fondo semitransparente
                //     ) {
                //         CircularProgressIndicator()
                //     }
                // }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun AddTransactionScreenPreview() {
        MaterialTheme { // Envuelve con MaterialTheme para que los componentes se vean correctamente
            AddTransactionScreen()
        }
    }