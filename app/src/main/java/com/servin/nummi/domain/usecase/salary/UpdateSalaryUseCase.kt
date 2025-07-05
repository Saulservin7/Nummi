package com.servin.nummi.domain.usecase.salary

import com.servin.nummi.domain.model.Salary
import com.servin.nummi.domain.repository.FinancialRepository
import javax.inject.Inject

class UpdateSalaryUseCase @Inject constructor(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(salary: Salary): Result<Unit> {
        return repository.updateSalary(salary)
    }
}
