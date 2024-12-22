package domain.method.utils

import domain.Matrix

abstract class CalculatePlanCost {
    open fun <T> calculatePlanCost(plan: Matrix<T>, cost: Matrix<Double>): Double {
        var result = 0.0
        val _plan: Matrix<Double> = plan.map { it.map { value -> if (value == "-") 0.0 else value as Double } }
        val planSize: Pair<Int, Int> = plan.map { Pair(plan.size, it.size) }.first()
        val costSize: Pair<Int, Int> = cost.map { Pair(cost.size, it.size) }.first()
        if (planSize.first != costSize.first || planSize.second != costSize.second) throw IllegalArgumentException("plan.size != cost.size (${planSize} != ${costSize})")

        for (y in 0 until planSize.first) {
            for (x in 0 until planSize.second) {
                result += (_plan[y][x] * cost[y][x])
            }
        }

        return result
    }
}