package lab5.domain.method


import lab5.app.StringResource
import lab5.app.StringResourceKeys
import lab5.domain.*
import lab5.domain.method.utils.CalculatePlanCost
import lab5.domain.method.utils.DegeneratePlanTransformer


class LeastCostMethod(
    override val config: ConfigurationOfSolution,
) : ReferencePlanMethod, CalculatePlanCost(), DegeneratePlanTransformer {
    override val name: String = StringResource.getStringResource(key = StringResourceKeys.leastCostMethod)

    private val transportationProblem = config.transportationProblem
    private val objectiveFunction = config.objectiveFunction

    private val unvisitedCells = mutableListOf<Coordinates>()

    private val _historyOfSolution = mutableListOf<ReferencePlanSolutionResult>()
    val historyOfSolution = _historyOfSolution.toList()

    override fun solution(): ReferencePlanSolutionResult {
        transportationProblem.checkForOpenAndCloseProblem()
        initUnvisitedCells()
        val resultPlan: MutableMatrix<Any> = initResultPlan()

        while (unvisitedCells.isNotEmpty()) {
            val suppliersAvailability = transportationProblem.suppliersAvailability
            val consumersExpectation = transportationProblem.consumersExpectation

            val costInformation = when (objectiveFunction) {
                ObjectiveFunction.MAXIMUM_PROFIT -> findMaxCost()
                ObjectiveFunction.MINIMUM_COST -> findMinCost()
            }
            val y = costInformation.coordinates.y
            val x = costInformation.coordinates.x

            unvisitedCells.remove(costInformation.value)

            if (calculateCost(suppliersAvailability[y], consumersExpectation[x]) < 0) {
                consumersExpectation[x] -= suppliersAvailability[y]
                resultPlan[y][x] = suppliersAvailability[y]
                suppliersAvailability[y] = 0.0
                punchOutHorizontalLineInMatrix(y, resultPlan)
            } else {
                suppliersAvailability[y] -= consumersExpectation[x]
                resultPlan[y][x] = consumersExpectation[x]
                consumersExpectation[x] = 0.0
                punchOutVerticalLineInMatrix(x, resultPlan)
                if (suppliersAvailability[y] == 0.0) {
                    punchOutHorizontalLineInMatrix(y, resultPlan)
                }
            }

            _historyOfSolution.add(
                ReferencePlanSolutionResult(
                    plan = resultPlan,
                    result = calculatePlanCost(resultPlan, transportationProblem.costsOfDelivery)
                )
            )
        }

        transportationProblem.plan = resultPlan
        if (!checkPlanForDegenerate(transportationProblem)) makePlanDegenerate()

        return ReferencePlanSolutionResult(
            plan = resultPlan,
            result = calculatePlanCost(resultPlan, transportationProblem.costsOfDelivery)
        )
    }

    override fun <T> calculatePlanCost(plan: Matrix<T>, cost: Matrix<Double>): Double = super.calculatePlanCost(plan, cost)

    override fun makePlanDegenerate() {
        val cellCoords =  when (objectiveFunction) {
            ObjectiveFunction.MAXIMUM_PROFIT -> getMaxCostWithoutValueInPlan()
            ObjectiveFunction.MINIMUM_COST -> getMinCostWithoutValueInPlan()
        }
        transportationProblem.plan!![cellCoords.y][cellCoords.x] = 0.0
    }

    /* Methods for Least cost */
    private fun calculateCost(supplierAvailabilityValue: Double, consumerExpectationValue: Double): Double =
        supplierAvailabilityValue - consumerExpectationValue

    private fun punchOutHorizontalLineInMatrix(y: Int, matrix: MutableMatrix<Any>) {
        unvisitedCells.removeAll { it.y == y }
        matrix[y].indices.forEach { x ->
            if (matrix[y][x] == 0.0) matrix[y][x] = "-"
        }
    }

    private fun punchOutVerticalLineInMatrix(x: Int, matrix: MutableMatrix<Any>) {
        unvisitedCells.removeAll { it.x == x }
        for (y in 0 until transportationProblem.suppliersCount) {
            if (matrix[y][x] == 0.0) matrix[y][x] = "-"
        }
    }

    private fun findMaxCost(): Cell { //cost: Matrix<Any>
        val costsOfDelivery = transportationProblem.costsOfDelivery.map { row -> row.map { it.toString().toDouble() } }
        var maxCost = Cell(coordinates =  Coordinates(-1, -1), value = -1.0)

        for (y in 0 until transportationProblem.suppliersCount) {
            for (x in 0 until transportationProblem.consumerCount) {
                if (costsOfDelivery[y][x] == -1.0) continue
                val coordinates = Coordinates(x = x, y = y)
                if (maxCost.value.toString().toDouble() < costsOfDelivery[y][x] && unvisitedCells.contains(coordinates)) {
                    maxCost = Cell(coordinates = coordinates, value = costsOfDelivery[y][x])
                }
            }
        }

        return maxCost
    }

    private fun getMaxCostWithoutValueInPlan(): Coordinates {
        val costsOfDelivery = transportationProblem.costsOfDelivery
        val plan = transportationProblem.plan
        var maxCost = Cell(coordinates = Coordinates(x = -1, y = -1), value = -1.0)

        for (y in 0 until transportationProblem.suppliersCount) {
            for (x in 0 until transportationProblem.consumerCount) {
                if (costsOfDelivery[y][x] == -1.0) continue
                if (maxCost.value.toString().toDouble() < costsOfDelivery[y][x] && plan!![y][x] == "-") {
                    maxCost = Cell(coordinates = Coordinates(x = x, y = y), value = costsOfDelivery[y][x])
                }
            }
        }

        return maxCost.coordinates
    }

    private fun findMinCost(): Cell {
        val costsOfDelivery = transportationProblem.costsOfDelivery
        val suppliersCount = transportationProblem.suppliersCount
        val consumersCount = transportationProblem.consumerCount
        var minCost = Cell(coordinates = Coordinates(-1, -1), value = Double.MAX_VALUE)

        val ignoreLastColumn = (0 until suppliersCount).all { y -> costsOfDelivery[y][consumersCount - 1] == 0.0 }
        val ignoreLastRow = (0 until consumersCount).all { x -> costsOfDelivery[suppliersCount - 1][x] == 0.0 }

        var unlockCell = false
        for (_y in 0 until suppliersCount - if (ignoreLastRow) 1 else 0) {
            for (_x in 0 until consumersCount - if (ignoreLastColumn) 1 else 0) {
                val y = if (ignoreLastRow && unlockCell) _y + 1 else _y
                val x = if (ignoreLastColumn && unlockCell) _x + 1 else _x

                if (ignoreLastColumn && unvisitedCells.all { it.y == y }) unlockCell = true
                if (ignoreLastRow && unvisitedCells.all { it.x == x }) unlockCell = true

                if (minCost.value.toString().toDouble() > costsOfDelivery[y][x] && unvisitedCells.contains(Coordinates(x = x, y = y))) {
                    minCost = Cell(coordinates = Coordinates(x = x, y = y), value = costsOfDelivery[y][x])
                }
            }
        }

        return minCost
    }

    private fun getMinCostWithoutValueInPlan(): Coordinates {
        val costsOfDelivery = transportationProblem.costsOfDelivery
        val plan = transportationProblem.plan
        var mixCost = Cell(coordinates = Coordinates(x = -1, y = -1), value = Double.MAX_VALUE)

        for (y in 0 until transportationProblem.suppliersCount) {
            for (x in 0 until transportationProblem.consumerCount) {
                if (costsOfDelivery[y][x] == 0.0) continue
                if (mixCost.value.toString().toDouble() > costsOfDelivery[y][x] && plan!![y][x] == "-") {
                    mixCost = Cell(coordinates = Coordinates(x = x, y = y), value = costsOfDelivery[y][x])
                }
            }
        }

        return mixCost.coordinates
    }

    /* utils for solution */
    private fun initResultPlan() = fillMatrix(
            cellsSize = Pair(transportationProblem.suppliersCount, transportationProblem.consumerCount),
            value = 0.0
        ).toMutableMatrix()

    private fun initUnvisitedCells() {
        for (y in 0 until transportationProblem.suppliersCount) {
            for (x in 0 until transportationProblem.consumerCount) {
                unvisitedCells.add(Coordinates(x = x, y = y))
            }
        }
    }
}









