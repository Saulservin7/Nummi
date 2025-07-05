package com.servin.nummi.domain.usecase.budget

import com.servin.nummi.domain.model.Budget
import com.servin.nummi.domain.repository.FinancialRepository
import javax.inject.Inject

class SaveBudgetUseCase @Inject constructor(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(budget: Budget): Result<Unit> {
        return repository.saveBudget(budget)
    }
}
