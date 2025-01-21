package lab5.domain.method

import lab5.domain.ConfigurationOfSolution
import lab5.domain.method.utils.TransportationProblemCloser

interface ReferencePlanMethod: TransportationProblemCloser {
    val name: String
    val config: ConfigurationOfSolution
    fun solution(): lab5.domain.ReferencePlanSolutionResult
}