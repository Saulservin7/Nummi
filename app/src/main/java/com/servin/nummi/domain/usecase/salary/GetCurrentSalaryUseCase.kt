package com.servin.nummi.domain.usecase.salary

import com.servin.nummi.domain.model.Salary
import com.servin.nummi.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentSalaryUseCase @Inject constructor(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(): Flow<Result<Salary?>> {
        return repository.getCurrentSalary()
    }
}
