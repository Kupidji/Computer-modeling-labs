package app


import domain.Cell
import domain.Coordinates
import domain.Matrix
import domain.print

interface Output {
    fun <T> showInputTable(rowHeaders: List<String>, columnHeaders: List<String>,matrix: Matrix<T>)
    fun <T> showTable(
        methodName: String? = null,
        rowHeaders: List<String>,
        columnHeaders: List<String>,
        matrix: Matrix<T>,
        result: T
    )
    fun <T> showPotentialMatrix(matrix: Matrix<T>)
    fun showCycleStartLocationHistory(cell: Cell)
    fun showCycleHistory(listCoordinates: List<Coordinates>)
    fun printFindOptimalPlanMsg()
    fun printOptimalPlanFoundMsg()
    fun printPlanAlreadyOptimalMsg()
    fun printHistoryOfSolutionMsg()
    fun printLine()
    fun printPotentialMatrixMsg()
    fun printHistoryOfCycleMsg()
}

class ConsoleOutput : Output {
    override fun <T> showInputTable(rowHeaders: List<String>, columnHeaders: List<String>, matrix: Matrix<T>) {
        println(StringResource.getStringResource(StringResourceKeys.inputData))
        matrix.print(rowHeaders, columnHeaders)
    }

    override fun <T> showTable(
        methodName: String?,
        rowHeaders: List<String>,
        columnHeaders: List<String>,
        matrix: Matrix<T>,
        result: T
    ) {
        println(
            if (methodName != null) "\"$methodName\". ${StringResource.getStringResource(StringResourceKeys.table)}"
            else StringResource.getStringResource(StringResourceKeys.table)
        )
        matrix.print(rowHeaders, columnHeaders)
        println("${StringResource.getStringResource(StringResourceKeys.result)}$result")
    }

    override fun <T> showPotentialMatrix(matrix: Matrix<T>) {
        println(StringResource.getStringResource(StringResourceKeys.potentialMatrix))
        for (list in matrix) {
            print("( ")
            for ((index, value) in list.withIndex()) {
                print("$value ")
                if (index != list.lastIndex) print("| ")
            }
            println(")")
        }
    }

    override fun showCycleStartLocationHistory(cell: Cell) {
        println("${StringResource.getStringResource(StringResourceKeys.cycleStart)}$cell")
    }

    override fun showCycleHistory(listCoordinates: List<Coordinates>) {
        val result = listCoordinates.joinToString(" -> ") { it.toString() }
        println("${StringResource.getStringResource(StringResourceKeys.cyclePath)}: $result")
    }

    override fun printFindOptimalPlanMsg() {
        printLine()
        println(StringResource.getStringResource(StringResourceKeys.searchOptimalSolution))
    }

    override fun printOptimalPlanFoundMsg() {
        println(StringResource.getStringResource(StringResourceKeys.optimalSolutionFound))
    }

    override fun printPlanAlreadyOptimalMsg() {
        println(StringResource.getStringResource(StringResourceKeys.planAlreadyOptimal))
    }

    override fun printHistoryOfSolutionMsg() {
        println(StringResource.getStringResource(StringResourceKeys.historyOfSolution))
    }

    override fun printLine() {
        println(StringResource.getStringResource(StringResourceKeys.lineSeparator))
    }

    override fun printPotentialMatrixMsg() {
        println(StringResource.getStringResource(StringResourceKeys.potentialMatrix))
    }

    override fun printHistoryOfCycleMsg() {
        println(StringResource.getStringResource(StringResourceKeys.cyclePath))
    }
}
