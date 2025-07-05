// Ruta: com.servin.nummi.domain.usecase.salary/SaveSalaryUseCase.kt
package com.servin.nummi.domain.usecase.salary

import com.servin.nummi.domain.model.Salary
import com.servin.nummi.domain.repository.FinancialRepository
import javax.inject.Inject
import kotlin.Result

class SaveSalaryUseCase @Inject constructor(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(salary: Salary): Result<Unit> = repository.saveSalary(salary)
}
