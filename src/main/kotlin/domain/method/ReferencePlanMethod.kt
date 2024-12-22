package domain.method

import domain.ConfigurationOfSolution
import domain.ReferencePlanSolutionResult
import domain.method.utils.TransportationProblemCloser

interface ReferencePlanMethod: TransportationProblemCloser {
    val name: String
    val config: ConfigurationOfSolution
    fun solution(): ReferencePlanSolutionResult
}