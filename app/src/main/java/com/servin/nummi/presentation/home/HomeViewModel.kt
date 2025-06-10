package com.servin.nummi.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.servin.nummi.domain.model.Transaction
import com.servin.nummi.domain.model.TransactionType
import com.servin.nummi.domain.usecase.transactions.AddTransactionUseCase
import com.servin.nummi.domain.usecase.transactions.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState(isLoading = true))

    val state: StateFlow<HomeState> = _state

    init {

    }


    private suspend fun getTransactions() {
        // Llamamos al caso de uso, que devuelve un Flow.
        getTransactionsUseCase()
            .onEach { transactions ->
                // Cada vez que el Flow emite una nueva lista (por un cambio en Firestore),
                // actualizamos nuestro estado con la nueva lista y desactivamos el loading.
                _state.value = HomeState(transactions = transactions)
            }
            .catch { error ->
                // Si el Flow emite un error, lo capturamos y actualizamos el estado.
                _state.value = HomeState(error = error.message)
            }
            .launchIn(viewModelScope) // Lanzamos la corrutina en el scope del ViewModel.
        // Se cancelará automáticamente cuando el ViewModel se destruya.
    }

    fun addExpense(amount: Double, category: String, description: String?) {
        // Lanzamos una corrutina para realizar la operación de red sin bloquear el hilo principal.
        viewModelScope.launch {
            try {
                // Creamos el objeto Transaction con los datos necesarios.
                val transaction = Transaction(
                    userId = auth.currentUser?.uid ?: "", // Obtenemos el ID del usuario actual.
                    amount = amount,
                    type = TransactionType.EXPENSE, // Es un gasto.
                    category = category,
                    description = description,
                    date = Date() // Usamos la fecha y hora actual.
                )
                // Llamamos al caso de uso para agregar la transacción.
                addTransactionUseCase(transaction)
                // Aquí podrías añadir lógica de éxito, como mostrar un Snackbar,
                // pero por ahora no es necesario, ya que el Flow se actualizará solo.
            } catch (e: Exception) {
                // Si la escritura falla, actualizamos el estado con un mensaje de error.
                // Podríamos usar un canal o evento separado para errores que no deben ser parte del estado permanente.
                _state.value = _state.value.copy(error = "Error al agregar el gasto: ${e.message}")
            }
        }
    }

}