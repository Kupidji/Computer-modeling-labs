package ru.kupidji

import java.util.*

/**
 * Главная точка входа в приложение, решающее транспортную задачу. Пользователь может выбрать
 * один из способов ввода данных (стандартные данные (условия задачи), ввод с клавиатуры, тестовые данные)
 * Затем выполняется расчет, результаты выводятся в консоль
 */
fun main() {
    // Инициализация сканера для пользовательского ввода
    val scanner = Scanner(System.`in`)

    // Используется обработчик ввода для консоли
    val input: InputHandler = ConsoleInputHandler(scanner)

    // Переменные для хранения данных таблицы
    lateinit var tableSize: Pair<Int, Int> // Размер таблицы (количество строк и столбцов)
    lateinit var stockTitles: List<String> // Наименования складов
    lateinit var requiredTitles: List<String> // Наименования пунктов назначения
    lateinit var stockValues: List<Double> // Остатки на складах
    lateinit var requiredValues: List<Double> // Потребности пунктов назначения
    lateinit var costs: Matrix<Double> // Матрица стоимости перевозок

    // Определение способа ввода данных
    val wayOfSolution = input.waysOfSolutionInput()

    when (wayOfSolution) {
        // Стандартные данные для задачи
        WaysOfSolution.DEFAULT -> {
            tableSize = Pair(4, 5) // Размер таблицы (4 строки и 5 столбцов)
            stockTitles = listOf("Мрамор", "Малахит", "Проволка", "Пластмасса")
            requiredTitles = listOf("А", "Б", "В", "Г", "Д")
            stockValues = listOf(17.0, 33.0, 15.0, 20.0) // Количество на складах
            requiredValues = listOf(10.0, 20.0, 15.0, 15.0, 20.0) // Требуемые количества
            costs = matrixOf( // Матрица стоимости перевозок
                tableSize.first,
                tableSize.second,
                90.0, 190.0, 140.0, 170.0, 90.0,
                40.0, 210.0, 270.0, 80.0, 290.0,
                122.0, 230.0, 40.0, 15.0, 240.0,
                100.0, 220.0, 80.0, 75.0, 270.0,
            )
        }

        // Ввод данных с клавиатуры
        WaysOfSolution.FILL_DATA -> {
            tableSize = input.tableSizeInput() // Запрос размера таблицы
            stockTitles = input.stockTitlesInput() // Запрос названий складов
            requiredTitles = input.requiredTitlesInput() // Запрос названий пунктов назначения
            stockValues = input.stockInput() // Ввод остатков
            requiredValues = input.requiredInput() // Ввод потребностей
            costs = input.costDeliveryInput() // Ввод стоимости перевозок
        }

        // Тестовые данные для задачи
        WaysOfSolution.TEST -> {
            tableSize = Pair(3, 4) // Размер таблицы (3 строки и 4 столбца)
            stockTitles = listOf("1", "2", "3")
            requiredTitles = listOf("А", "Б", "В", "Г")
            stockValues = listOf(160.0, 140.0, 170.0) // Остатки на складах
            requiredValues = listOf(120.0, 50.0, 190.0, 110.0) // Потребности
            costs = matrixOf( // Матрица стоимости перевозок
                tableSize.first,
                tableSize.second,
                7.0, 8.0, 1.0, 2.0,
                4.0, 5.0, 9.0, 8.0,
                9.0, 2.0, 3.0, 6.0,
            )
        }
    }

    // Создание задачи транспортной оптимизации
    val transportationProblem: TransportationProblem = TransportationProblemSolution(
        stockSize = tableSize.first, // Количество строк (склады)
        stockTitles = stockTitles, // Названия складов
        requiredSize = tableSize.second, // Количество столбцов (пункты назначения)
        requiredTitles = requiredTitles, // Названия пунктов назначения
        stock = stockValues.toMutableList(), // Остатки на складах
        required = requiredValues.toMutableList(), // Потребности
        cost = costs, // Матрица стоимости перевозок
    )

    // Метод решения задачи (метод наименьшей стоимости)
    val method = LeastCostMethod(transportationProblem = transportationProblem)

    // Решение задачи
    val problemSolution = transportationProblem.solve(method = method)
    val resultMatrix = problemSolution.first // Результирующая матрица
    val resultSolution = problemSolution.second // Итоговая стоимость

    // Вывод результатов в консоль
    val output = ConsoleOutput()
    output.showInputTable(rowHeaders = stockTitles, columnHeaders = requiredTitles, matrix = transportationProblem.cost) // Исходная таблица
    output.showTable(method = method, rowHeaders = stockTitles, columnHeaders = requiredTitles, matrix = resultMatrix, resultSolution) // Итоги решения
}
