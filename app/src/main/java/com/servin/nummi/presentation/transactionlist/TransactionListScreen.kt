package com.servin.nummi.presentation.transactionlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servin.nummi.domain.model.CategoryType
import com.servin.nummi.domain.model.Transaction
import com.servin.nummi.domain.model.TransactionListScreenState
import com.servin.nummi.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Transacciones") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        TransactionListContent(
            uiState = uiState,
            paddingValues = paddingValues
        )
    }
}

@Composable
fun TransactionListContent(
    uiState: TransactionListScreenState,
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            ErrorState(errorMessage = uiState.error, paddingValues = paddingValues)
        } else if (uiState.transaction.isEmpty()) {
            EmptyState(paddingValues = paddingValues)
        } else {
            TransactionList(transactions = uiState.transaction, listPaddingValues = PaddingValues(0.dp) /* Adjusted padding */)
        }
    }
}


@Composable
fun TransactionList(transactions: List<Transaction>, listPaddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(listPaddingValues) // Use the passed padding
            .padding(horizontal = 16.dp), // Keep horizontal padding for items
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp) // Padding for the content within LazyColumn
    ) {
        items(transactions) { transaction ->
            TransactionItem(transaction = transaction)
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description ?: transaction.category.type,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Categoría: ${transaction.category.type}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Fecha: ${dateFormat.format(transaction.date)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "${if (transaction.type == TransactionType.EXPENSE) "-" else "+"}${
                    String.format(
                        Locale.US,
                        "%.2f",
                        transaction.amount
                    )
                }",
                style = MaterialTheme.typography.titleMedium,
                color = if (transaction.type == TransactionType.EXPENSE) MaterialTheme.colorScheme.error else Color(
                    0xFF006400
                ) // DarkGreen
            )
        }
    }
}

@Composable
fun EmptyState(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // This padding is from Scaffold
            .padding(16.dp), // Additional padding for content
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "No hay transacciones registradas.",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Agrega una nueva transacción para comenzar.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ErrorState(errorMessage: String, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TransactionListScreenPreview_WithData() {
    MaterialTheme {
        // Simulate state with data for preview
        val previewState = TransactionListScreenState(
            transaction = listOf(
                Transaction("1", "user1", 100.0, TransactionType.EXPENSE, CategoryType.FOOD, "Almuerzo", Date()),
                Transaction("2", "user1", 50.0, TransactionType.INCOME, CategoryType.OTHER, "Regalo", Date())
            ),
            isLoading = false,
            error = null
        )
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mis Transacciones") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            TransactionListContent(uiState = previewState, paddingValues = paddingValues)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TransactionListScreenPreview_Empty() {
    MaterialTheme {
        val previewState = TransactionListScreenState(isLoading = false, error = null)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mis Transacciones") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            TransactionListContent(uiState = previewState, paddingValues = paddingValues)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TransactionListScreenPreview_Loading() {
    MaterialTheme {
        val previewState = TransactionListScreenState(isLoading = true)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mis Transacciones") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            TransactionListContent(uiState = previewState, paddingValues = paddingValues)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TransactionListScreenPreview_Error() {
    MaterialTheme {
        val previewState = TransactionListScreenState(isLoading = false, error = "No se pudieron cargar las transacciones.")
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mis Transacciones") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            TransactionListContent(uiState = previewState, paddingValues = paddingValues)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TransactionItemPreview() {
    MaterialTheme {
        TransactionItem(
            transaction = Transaction(
                id = "1",
                userId = "user1",
                amount = 125.50,
                type = TransactionType.EXPENSE,
                category = CategoryType.FOOD,
                description = "Almuerzo con amigos en el centro comercial del sur",
                date = Date()
            )
        )
    }
}