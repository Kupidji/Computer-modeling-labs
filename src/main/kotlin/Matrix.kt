package ru.kupidji

typealias Matrix<T> = List<List<T>>

fun <T> matrixOf(columns: Int, rows: Int, vararg value: T): Matrix<T> {
    val matrix = mutableListOf<MutableList<T>>()
    val list = value.toMutableList()

    if (list.size != columns*rows) throw IllegalArgumentException("Размер матрицы должен быть ${columns*rows}, но введено лишь ${list.size} элементов")

    var start = 0
    var end = rows
    repeat(columns) {
        matrix.add(list.subList(start, end))
        start += rows
        end += rows
    }
    return matrix
}

fun <T> Matrix<T>.print(
    rowHeaders: List<String>,    // Заголовки строк
    columnHeaders: List<String>, // Заголовки столбцов
    table: List<List<T>>         // Данные таблицы
) {
    // Находим максимальную длину строки в каждой колонке (с учётом заголовков)
    val columnWidths = IntArray(columnHeaders.size + 1) { columnIndex ->
        if (columnIndex == 0) {
            rowHeaders.maxOf { it.length } + 1 // Учитываем отступ для заголовков строк
        } else {
            maxOf(columnHeaders[columnIndex - 1].length, table.maxOf { it[columnIndex - 1].toString().length })
        }
    }

    // Печать верхней рамки с заголовками столбцов
    printRowSeparator(columnWidths)

    // Печать заголовков столбцов
    print("|")
    print(" ".padEnd(columnWidths[0])) // Пустая ячейка перед заголовками столбцов
    for (j in columnHeaders.indices) {
        val paddedHeader = columnHeaders[j].padStart(columnWidths[j + 1])
        print(" $paddedHeader |")
    }
    println()

    // Печать разделителя после заголовков столбцов
    printRowSeparator(columnWidths)

    // Печать строк таблицы с выравниванием и заголовками строк
    for (i in table.indices) {
        // Печать заголовка строки
        print("| ${rowHeaders[i].padEnd(columnWidths[0] - 1)} |")

        // Печать данных строки
        for (j in table[i].indices) {
            val cell = table[i][j].toString()
            val paddedCell = cell.padStart(columnWidths[j + 1])
            print(" $paddedCell |")
        }
        println()

        // Печать разделителя после строки
        printRowSeparator(columnWidths)
    }
}

fun printRowSeparator(columnWidths: IntArray) {
    println("+" + columnWidths.joinToString("+") { "-".repeat(it + 2) } + "+")
}

fun fillMatrix(cellsSize: Pair<Int, Int>, list: List<Variable>): Matrix<Any> {
    val result: MutableList<MutableList<Any>> = mutableListOf()
    repeat(cellsSize.first) {
        val temp = mutableListOf<Any>()
        repeat(cellsSize.second) {
            temp.add("-")
        }
        result.add(temp)
    }
    for (variable in list) {
        result[variable.stock][variable.required] = variable.value
    }
    return result
}