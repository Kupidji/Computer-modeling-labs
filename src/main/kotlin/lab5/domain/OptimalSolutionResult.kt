package lab5.domain

sealed class OptimalSolutionResult {
    data class OptimalPlanWithResult(val plan: Matrix<Any>, val result: Double): OptimalSolutionResult()
    data class UnchangedPlanWithResult(val plan: Matrix<Any>, val result: Double): OptimalSolutionResult()
}