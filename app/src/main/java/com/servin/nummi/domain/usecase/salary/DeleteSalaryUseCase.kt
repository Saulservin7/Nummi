package com.servin.nummi.domain.usecase.salary

import com.servin.nummi.domain.repository.FinancialRepository
import javax.inject.Inject

class DeleteSalaryUseCase @Inject constructor(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(salaryId: String): Result<Unit> {
        // Assuming "current" is the ID for the salary to be deleted as per repository implementation
        return repository.deleteSalary(salaryId)
    }
}
