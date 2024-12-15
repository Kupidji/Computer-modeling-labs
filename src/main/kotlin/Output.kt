package ru.kupidji

interface Output {
    fun <T> showInputTable(rowHeaders: List<String>, columnHeaders: List<String>,matrix: Matrix<T>)
    fun <T> showTable(
        method: TransportationProblemSolutionMethod,
        rowHeaders: List<String>,
        columnHeaders: List<String>,
        matrix: Matrix<T>,
        result: T
    )
}

class ConsoleOutput : Output {
    override fun <T> showInputTable(rowHeaders: List<String>, columnHeaders: List<String>, matrix: Matrix<T>) {
        println("Введенные данные")
        matrix.print(rowHeaders, columnHeaders, matrix)
    }

    override fun <T> showTable(
        method: TransportationProblemSolutionMethod,
        rowHeaders: List<String>,
        columnHeaders: List<String>,
        matrix: Matrix<T>,
        result: T
    ) {
        println("\"${method.name}\". Исходная матрица")
        matrix.print(rowHeaders, columnHeaders, matrix)
        println("Результат: $result")
    }
}