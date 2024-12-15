package ru.kupidji

import java.util.*

interface InputHandler {
    fun waysOfSolutionInput(): WaysOfSolution
    fun tableSizeInput(): Pair<Int, Int>
    fun stockTitlesInput(): List<String>
    fun requiredTitlesInput(): List<String>
    fun stockInput(): List<Double>
    fun requiredInput(): List<Double>
    fun costDeliveryInput(): Matrix<Double>
}

enum class WaysOfSolution {
    DEFAULT,
    FILL_DATA,
    TEST,
}

class ConsoleInputHandler(
    private val scanner: Scanner,
): InputHandler {
    var countOfStocks = 0
        private set
    var countOfRequired = 0
        private set

    override fun waysOfSolutionInput(): WaysOfSolution {
        println(
            """
            Вычисление оптимального плана для объединения "Труд"
            Пути решения:
            1) С дефолтными значениями
            2) Задать значения самостоятельно
            3) Тест
        """.trimIndent()
        )
        return when (val number = numberInput("Выберите путь (1, 3)", upBound = 3.0).toInt()) {
            1 -> WaysOfSolution.DEFAULT
            2 -> WaysOfSolution.FILL_DATA
            3 -> WaysOfSolution.TEST
            else -> throw IllegalArgumentException("got $number but 1, 2 or 3 was expected")
        }
    }

    override fun tableSizeInput(): Pair<Int, Int> {
        countOfStocks = numberInput("Поставщики").toInt()
        countOfRequired = numberInput("Покупатели").toInt()
        return Pair(
            countOfStocks,
            countOfRequired
        )
    }

    override fun stockTitlesInput(): List<String> = titleInput("Введите имена поставщиков", countOfStocks)

    override fun requiredTitlesInput(): List<String> = titleInput("Введите имена заказчиков", countOfRequired)

    override fun stockInput(): List<Double> {
        val result = mutableListOf<Double>()
        println("Введите запасы поставщиков:")
        for (i in 0 until countOfStocks) {
            print("${i + 1}) ")
            result.add(numberInput())
        }
        return result
    }

    override fun requiredInput(): List<Double> {
        val result = mutableListOf<Double>()
        println("Введите потребности покупателей:")
        for (i in 0 until countOfRequired) {
            print("${i + 1}) ")
            result.add(numberInput())
        }
        return result
    }

    override fun costDeliveryInput(): Matrix<Double> {
        val result: MutableList<MutableList<Double>> = mutableListOf()
        println("Введите стоимость доставки:")
        for (i in 0 until countOfStocks) {
            val rowList = mutableListOf<Double>()
            for (j in 0 until countOfRequired) {
                print("Ячейка ${i + 1}:${j + 1} - ")
                rowList.add(numberInput())
            }
            result.add(rowList)
        }
        return result
    }

    private fun titleInput(text: String, count: Int): List<String> {
        val result = mutableListOf<String>()
        println("$text - ")
        repeat(count) {
            print("${it + 1}) ")
            while (true) {
                val title = scanner.next()
                if (title.isBlank())
                    println("Название не дожно быть пустым!")
                else {
                    result.add(title)
                    break
                }
            }
        }
        return result
    }

    private fun numberInput(
        text: String? = null,
        upBound: Double = Double.MAX_VALUE,
        inputRangeError: String = "Число должно входить в границы от 0 до $upBound ",
        inputArgumentError: String = "Ошибка ввода. Введите корректное число",
    ): Double {
        var result: Double
        while (true) {
            if (text != null) print("$text - ")
            try {
                result = scanner.nextDouble()
                if (checkValue(result, upBound = upBound)) break else println(inputRangeError)
            } catch (e: Exception) {
                println(inputArgumentError)
                scanner.nextLine()
            }
        }
        return result
    }

    private fun checkValue(value: Number, upBound: Number): Boolean = value as Double in 0.0..upBound as Double

}