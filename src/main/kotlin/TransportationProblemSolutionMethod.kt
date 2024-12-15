package ru.kupidji

import kotlin.math.min

/* author of LeasCostMethod - https://github.com/isstaif/TransportationProblem/blob/master/TransportationProblem.java */

sealed interface TransportationProblemSolutionMethod {
    val name: String
    val transportationProblem: TransportationProblem
    fun solution(): Pair<Matrix<Any>, Double>
}

class LeastCostMethod(
    override val transportationProblem: TransportationProblem,
) : TransportationProblemSolutionMethod {
    override val name: String = "Метод наименьшей стоимости"

    private val feasible: MutableList<Variable> = mutableListOf()

    init {
        for (i in 0 until (transportationProblem.requiredSize + transportationProblem.stockSize - 1)) feasible.add(Variable())
    }

    override fun solution(): Pair<Matrix<Any>, Double> {
        with(transportationProblem) {
            var min: Double
            var k = 0

            val isSet = MutableList(stockSize) { MutableList(requiredSize) { false } }

            var i: Int
            var j: Int
            val minCost = Variable()

            while (k < (stockSize + requiredSize - 1)) {
                minCost.value = Double.MAX_VALUE

                for (m in 0 until stockSize) {
                    for (n in 0 until requiredSize) {
                        if (!isSet[m][n] && cost[m][n] < minCost.value) {
                            minCost.stock = m
                            minCost.required = n
                            minCost.value = cost[m][n]
                        }
                    }
                }

                i = minCost.stock
                j = minCost.required

                min = min(required[j], stock[i])

                feasible[k].required = j
                feasible[k].stock = i
                feasible[k].value = min
                k++

                required[j] -= min
                stock[i] -= min

                if (stock[i] == 0.0) {
                    for (l in 0 until requiredSize) isSet[i][l] = true
                } else {
                    for (l in 0 until stockSize) isSet[l][j] = true
                }
            }

            var solution = 0.0
            for (x in feasible) {
                solution += x.value * cost[x.stock][x.required]
            }

            val matrix = fillMatrix(Pair(stockSize, requiredSize) , feasible)

            return Pair(
                matrix,
                solution
            )
        }
    }
}

class Variable {
    var stock: Int = 0
    var required: Int = 0
    var value: Double = 0.0

    override fun toString(): String {
        return "x[${stock + 1},${required + 1}]=$value"
    }
}

