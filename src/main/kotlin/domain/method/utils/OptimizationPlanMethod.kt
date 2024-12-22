package domain.method.utils

import domain.ConfigurationOfSolution
import domain.OptimalSolutionResult


interface OptimizationPlanMethod {
    val name: String
    val config: ConfigurationOfSolution
    fun solution(): OptimalSolutionResult
}