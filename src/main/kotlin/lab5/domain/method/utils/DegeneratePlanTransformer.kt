package lab5.domain.method.utils

import lab5.domain.TransportationProblem


interface DegeneratePlanTransformer {
    /**
     * @return true if count of filled cells equals sum of supplier count and consumer count - 1. false otherwise
     */
    fun checkPlanForDegenerate(transportationProblem: TransportationProblem): Boolean {
        val countOfFilledCells = transportationProblem.plan?.map { list -> list.filter { value -> value != "-" }.size }?.sum()
        return (countOfFilledCells == transportationProblem.suppliersCount + transportationProblem.consumerCount - 1)
    }

    fun makePlanDegenerate()
}