package lab5.domain.method

import lab5.app.StringResource
import lab5.app.StringResourceKeys
import lab5.domain.*
import lab5.domain.method.utils.CalculatePlanCost

class ModiMethod(
    override val config: ConfigurationOfSolution,
): OptimizationPlanMethod, CalculatePlanCost() {
    override val name: String = StringResource.getStringResource(key = StringResourceKeys.modiMethod)

    private val transportationProblem = config.transportationProblem
    private val objectiveFunction = config.objectiveFunction

    private var supplierCount: Int = transportationProblem.suppliersCount
    private var consumerCount: Int = transportationProblem.consumerCount
    private val costMatrix = transportationProblem.costsOfDelivery
    private val plan = transportationProblem.plan?.toMutableMatrix() ?: throw IllegalStateException("To use Modi method, plan must be initialized")
    private var hasCycle = false

    /**
     * @return history of the potential matrix used to check if the plan is optimal.
     *  * The plan is considered optimal if the potential matrix contains no negative values.
     * */
    private var _historyOfPotentialMatrix = mutableListOf<Matrix<Double>>()
    val historyOfPotentialMatrix: List<Matrix<Double>>
        get() = _historyOfPotentialMatrix.toList()


    private var _historyOfCycleInitialLocation = mutableListOf<Cell>()
    /**
     * @return history of initial values and locations of cycle as Pair<value, Pair<y, x>>
     * */
    val historyOfCycleInitialLocation: List<Cell>
        get() = _historyOfCycleInitialLocation.toList()


    private var _historyOfCyclePath =  mutableListOf<List<Coordinates>>()
    /**
     * @return history of initial values and locations of cycle as Pair<value, Pair<y, x>>
     * */
    val historyOfCyclePath: List<List<Coordinates>>
        get() = _historyOfCyclePath.toList()


    private var _historyOfPlanWithResultCost = mutableListOf<Pair<Matrix<Any>, Double>>()
    /**
     * @return plan change history
     * */
    val historyOfPlanWithResultCost: List<Pair<Matrix<Any>, Double>>
        get() = _historyOfPlanWithResultCost.toList()


    override fun solution(): OptimalSolutionResult {
        while (true) {
            val uvValues = calculatePotentials()
            val Cij = when (objectiveFunction) {
                ObjectiveFunction.MAXIMUM_PROFIT -> calculateCijForMaxProfit(uvValues.first, uvValues.second)
                ObjectiveFunction.MINIMUM_COST -> calculateCijForMinCost(uvValues.first, uvValues.second)
            }
            _historyOfPotentialMatrix.add(Cij)

            val cycleInitialLocation =
                when (objectiveFunction) {
                    ObjectiveFunction.MAXIMUM_PROFIT -> findCycleInitialLocationForMaxProfit(Cij)
                    ObjectiveFunction.MINIMUM_COST -> findCycleInitialLocationForMinCost(Cij)
                }
            _historyOfCycleInitialLocation.add(Cell(coordinates = cycleInitialLocation.coordinates, value = cycleInitialLocation.value))


            when (objectiveFunction) {
                ObjectiveFunction.MAXIMUM_PROFIT -> if (isPlanOptimalForMaxProfit(value = cycleInitialLocation.value.toString().toDouble())) break
                ObjectiveFunction.MINIMUM_COST -> if (isPlanOptimalForMinCost(value = cycleInitialLocation.value.toString().toDouble())) break
            }

            hasCycle = false // убираем старое значение

            val plan = adjustPlanBasedOnCycle(coordinates = cycleInitialLocation.coordinates)
            _historyOfPlanWithResultCost.add(Pair(plan.map { it.toList() }, calculatePlanCost(plan, costMatrix))) //deep copy

        }

        if (_historyOfPlanWithResultCost.lastOrNull() == null)
            return OptimalSolutionResult.UnchangedPlanWithResult(plan = plan, result = calculatePlanCost(plan, costMatrix))

        return OptimalSolutionResult.OptimalPlanWithResult(plan = _historyOfPlanWithResultCost.last().first, result = _historyOfPlanWithResultCost.last().second)
    }

    override fun <T> calculatePlanCost(plan: Matrix<T>, cost: Matrix<Double>): Double {
        return super.calculatePlanCost(plan, cost)
    }

    /**
     * @return uValues and vValues
     * */
    private fun calculatePotentials(): Pair<List<Double>, List<Double>> {
        val uValues = MutableList(supplierCount) { Double.MAX_VALUE }
        val vValues = MutableList(consumerCount) { Double.MAX_VALUE }
        vValues[consumerCount - 1] = 0.0

        while (uValues.contains(Double.MAX_VALUE) || vValues.contains(Double.MAX_VALUE)) {
            for (x in consumerCount - 1 downTo 0) {
                if (vValues[x] == Double.MAX_VALUE) continue
                for (y in supplierCount - 1 downTo 0) {
                    if (plan[y][x] == "-") continue
                    if (uValues[y] == Double.MAX_VALUE) {
                        uValues[y] = costMatrix[y][x] - vValues[x]
                    }
                }
            }

            for (y in supplierCount - 1 downTo 0) {
                if (uValues[y] == Double.MAX_VALUE) continue
                for (x in consumerCount - 1 downTo 0) {
                    if (plan[y][x] == "-") continue
                    if (vValues[x] == Double.MAX_VALUE) {
                        vValues[x] = costMatrix[y][x] - uValues[y]
                    }
                }
            }
        }

        return Pair(uValues, vValues)
    }

    /**
     * @return coefficient matrix * using for check is plan optimal
     */
    private fun calculateCijForMaxProfit(uValues: List<Double>, vValues: List<Double>): Matrix<Double> {
        val result: MutableMatrix<Double> = mutableListOf()
        for (y in 0 until supplierCount) {
            val tempList = mutableListOf<Double>()
            for (x in 0 until consumerCount) {
                val Cij = costMatrix[y][x] - (uValues[y] + vValues[x])
                tempList.add(Cij)
            }
            result.add(tempList)
        }
        return result
    }

    /**
     * @return coords of max negative number and value
     */
    private fun findCycleInitialLocationForMaxProfit(potentialMatrix: Matrix<Double>): Cell {
        var maxProfit = Double.MIN_VALUE
        var location = Coordinates(x = 0, y = 0)
        for (y in 0 until supplierCount) {
            for (x in 0 until consumerCount) {
                if (potentialMatrix[y][x] > maxProfit) {
                    maxProfit = potentialMatrix[y][x]
                    location = Coordinates(x = x, y = y)
                }
            }
        }
        if (maxProfit == Double.MIN_VALUE) maxProfit = 0.0
        return Cell(coordinates = location, value = maxProfit,)
    }

    /**
     * @return true if value is negative, false otherwise.
     */
    private fun isPlanOptimalForMaxProfit(value: Double) = (value <= 0.0)

    private fun calculateCijForMinCost(uValues: List<Double>, vValues: List<Double>): Matrix<Double> {
        val result: MutableMatrix<Double> = mutableListOf()
        for (y in 0 until supplierCount) {
            val tempList = mutableListOf<Double>()
            for (x in 0 until consumerCount) {
                val Cij = costMatrix[y][x] - (uValues[y] + vValues[x])
                tempList.add(Cij)
            }
            result.add(tempList)
        }

        return result
    }

    /**
     * @return coords of min negative number and value
     * */
    private fun findCycleInitialLocationForMinCost(potentialMatrix: Matrix<Double>): Cell {
        var maxCost = Double.MAX_VALUE
        var location = Coordinates(x = 0, y = 0)
        for (y in 0 until supplierCount) {
            for (x in 0 until consumerCount) {
                if (potentialMatrix[y][x] < maxCost) {
                    maxCost = potentialMatrix[y][x]
                    location = Coordinates(x = x, y = y)
                }
            }
        }

        return Cell(coordinates = location, value = maxCost)
    }

    /***
     * @return true if value is positive, false otherwise
     */
    private fun isPlanOptimalForMinCost(value: Double) = (value >= 0.0)

    private fun adjustPlanBasedOnCycle(coordinates: Coordinates): Matrix<Any> {
        val minPosY = coordinates.y
        val minPosX = coordinates.x

        plan[minPosY][minPosX] = 0.0
        var cycle = mutableListOf<Pair<Int, Int>>()

        // Поиск цикла
        dfsForCycle(
            newPlan = plan,
            cycle = cycle,
            posY = minPosY,
            posX = minPosX,
            startPosY = minPosY,
            startPosX = minPosX,
            prevPos = null,
            prevAction = "",
        )

        if (!hasCycle) {
            throw IllegalStateException("Cycle not found!")
        }

        cycle = removeIntermediateCells(cycle).toMutableList()

        var minMinus = Double.MAX_VALUE
        if (cycle.isEmpty()) { throw IllegalStateException("Cycle is empty") }
        for (i in cycle.indices) {
            if (i % 2 != 0) {
                val (y, x) = cycle[i]
                val cellValue = plan[y][x] as Double
                if (cellValue < minMinus) {
                    minMinus = cellValue
                }
            }
        }

        _historyOfCyclePath.add(cycle.map { Coordinates(x = it.second, y = it.first) })

        for (i in cycle.indices) {
            val (y, x) = cycle[i]
            val currentValue = if (plan[y][x] == "-") 0.0 else plan[y][x] as Double

            if (i % 2 == 0) plan[y][x] = currentValue + minMinus
            else plan[y][x] = currentValue - minMinus

            if (plan[y][x] == 0.0) plan[y][x] = "-"
        }

        return plan
    }

    private fun dfsForCycle(
        newPlan: List<MutableList<Any>>,
        cycle: MutableList<Pair<Int, Int>>,
        posY: Int,
        posX: Int,
        startPosX: Int,
        startPosY: Int,
        prevPos: Pair<Int, Int>?,
        prevAction: String
    ) {
        if (hasCycle) return
        if (posY >= newPlan.size || posX >= newPlan.first().size || posY < 0 || posX < 0) return
        if (posY == startPosY && posX == startPosX && cycle.size > 2) {
            hasCycle = true
            return
        }

        cycle.add(Pair(posY, posX))

        // Right
        for (x in posX + 1 until newPlan[posY].size) {
            if (newPlan[posY][x] != "-" &&
                Pair(posY, x) != prevPos &&
                prevAction != "L"
            ) {
                dfsForCycle(
                    newPlan = newPlan,
                    cycle = cycle,
                    posY = posY,
                    posX = x,
                    startPosY = startPosY,
                    startPosX = startPosX,
                    prevPos = Pair(posY, posX),
                    prevAction = "R",
                )
            }
        }

        // Left
        for (x in posX - 1 downTo 0) {
            if (newPlan[posY][x] != "-" &&
                Pair(posY, x) != prevPos &&
                prevAction != "R"
            ) {
                dfsForCycle(
                    newPlan = newPlan,
                    cycle = cycle,
                    posY = posY,
                    posX = x,
                    startPosY = startPosY,
                    startPosX = startPosX,
                    prevPos = Pair(posY, posX),
                    prevAction = "L",
                )
            }
        }

        // Up
        for (y in posY - 1 downTo 0) {
            if (newPlan[y][posX] != "-" &&
                Pair(y, posX) != prevPos &&
                prevAction != "D"
            ) {
                dfsForCycle(
                    newPlan = newPlan,
                    cycle = cycle,
                    posY = y,
                    posX = posX,
                    startPosY = startPosY,
                    startPosX = startPosX,
                    prevPos = Pair(posY, posX),
                    prevAction = "U",
                )
            }
        }

        // Down
        for (y in posY + 1 until newPlan.size) {
            if (newPlan[y][posX] != "-" &&
                Pair(y, posX) != prevPos &&
                prevAction != "U"
            ) {
                dfsForCycle(
                    newPlan = newPlan,
                    cycle = cycle,
                    posY = y,
                    posX = posX,
                    startPosY = startPosY,
                    startPosX = startPosX,
                    prevPos = Pair(posY, posX),
                    prevAction = "D",
                )
            }
        }

        if (!hasCycle) {
            cycle.removeLast()
        }
    }

    private fun removeIntermediateCells(cycle: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        fun horizontalCheck(): List<Pair<Int, Int>> {
            val _cycle = cycle.plus(Pair(-1, -1))
            val result = mutableListOf<Pair<Int, Int>>()
            val tempList = mutableListOf<Pair<Int, Int>>()

            for (i in 1.._cycle.lastIndex) {
                val prevPair = _cycle[i-1]
                val currentPair = _cycle[i]

                if (prevPair.first == currentPair.first) {
                    tempList.add(prevPair)
                } else {
                    tempList.add(prevPair)
                    if (tempList.size >= 2) {
                        val cutInsideList = listOf(tempList.first(), tempList.last())
                        result.addAll(cutInsideList)
                    }
                    tempList.clear()
                }
            }

            return result
        }

        fun verticalCheck(): List<Pair<Int, Int>> {
            val _cycle = cycle.plus(Pair(-1, -1))
            val result = mutableListOf<Pair<Int, Int>>()
            val tempList = mutableListOf<Pair<Int, Int>>()

            for (i in 1.._cycle.lastIndex) {
                val prevPair = _cycle[i-1]
                val currentPair = _cycle[i]

                if (prevPair.second == currentPair.second) {
                    tempList.add(prevPair)
                } else {
                    tempList.add(prevPair)
                    if (tempList.size >= 2) {
                        val cutInsideList = listOf(tempList.first(), tempList.last())
                        result.addAll(cutInsideList)
                    }
                    tempList.clear()
                }
            }

            return result
        }

        return (horizontalCheck().toSet() + verticalCheck().toSet()).toList()
    }

}