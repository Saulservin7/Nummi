package com.servin.nummi.domain.usecase.budget

import com.servin.nummi.domain.model.Budget
import com.servin.nummi.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetUseCase @Inject constructor(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(): Flow<Result<Budget?>> {
        return repository.getCurrentBudget()
    }
}
