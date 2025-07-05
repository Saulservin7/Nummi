// Ruta: com.servin.nummi.domain.usecase.salary/GetSalaryUseCase.kt
package com.servin.nummi.domain.usecase.salary

import com.servin.nummi.domain.model.Salary
import com.servin.nummi.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.Result

class GetSalaryUseCase @Inject constructor(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(): Flow<Result<Salary?>> = repository.getCurrentSalary()
}
