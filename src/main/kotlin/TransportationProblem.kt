package ru.kupidji

import kotlin.math.min

interface TransportationProblem {
    val stockSize: Int
    val stockTitles: List<String>
    val requiredSize: Int
    val requiredTitles: List<String>
    val required: MutableList<Double>
    val stock: MutableList<Double>
    val cost: Matrix<Double>
}

class TransportationProblemSolution(
    override val stockSize: Int,
    override val stockTitles: List<String>,
    override val requiredSize: Int,
    override val requiredTitles: List<String>,
    override val required: MutableList<Double>,
    override val stock: MutableList<Double>,
    override val cost: Matrix<Double>,
) : TransportationProblem

fun TransportationProblem.solve(method: TransportationProblemSolutionMethod) = method.solution()
