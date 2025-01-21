package lab5.domain.method

import lab5.domain.ConfigurationOfSolution
import lab5.domain.OptimalSolutionResult

interface OptimizationPlanMethod {
    val name: String
    val config: ConfigurationOfSolution
    fun solution(): OptimalSolutionResult
}