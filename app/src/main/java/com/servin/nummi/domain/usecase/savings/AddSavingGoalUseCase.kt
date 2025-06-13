package com.servin.nummi.domain.usecase.savings

import com.servin.nummi.domain.model.SavingGoal
import com.servin.nummi.domain.repository.FinancialRepository
import javax.inject.Inject

class AddSavingGoalUseCase @Inject constructor(
    private val financialRepository: FinancialRepository
) {
    suspend operator fun invoke(
        savingGoal: SavingGoal
    ) = financialRepository.addSavingGoal(
        savingGoal

    )

}