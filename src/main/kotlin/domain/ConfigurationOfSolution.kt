package domain


class ConfigurationOfSolution(
    val transportationProblem: TransportationProblem,
    val objectiveFunction: ObjectiveFunction
)

enum class ObjectiveFunction {
    MAXIMUM_PROFIT,
    MINIMUM_COST;
}