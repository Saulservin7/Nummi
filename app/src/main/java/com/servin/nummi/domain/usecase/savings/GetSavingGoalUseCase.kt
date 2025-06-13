package com.servin.nummi.domain.usecase.savings

import com.servin.nummi.domain.model.SavingGoal
import com.servin.nummi.domain.model.Transaction
import com.servin.nummi.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavingGoalUseCase @Inject constructor(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(): Flow<Result<List<SavingGoal>>> = repository.getSavingGoals()

}