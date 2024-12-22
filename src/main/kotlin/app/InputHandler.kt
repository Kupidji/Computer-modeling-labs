package app

import domain.Matrix
import domain.ObjectiveFunction
import domain.WayOfSolution
import java.util.*

interface InputHandler {
    fun languageInput(): Language
    fun waysOfSolutionInput(): WayOfSolution
    fun objectiveFunctionInput(): ObjectiveFunction
    fun tableSizeInput(): Pair<Int, Int>
    fun suppliersTitleInput(): List<String>
    fun consumersTitleInput(): List<String>
    fun supplierAvailabilityInput(): List<Double>
    fun consumerExpectationInput(): List<Double>
    fun costsOfDeliveryInput(): Matrix<Double>
    fun showHistoryOrExitInput(): Boolean
}

class ConsoleInputHandler(
    private val scanner: Scanner,
) : InputHandler {
    lateinit var language: Language
    var countOfStocks = 0
        private set
    var countOfRequired = 0
        private set

    override fun languageInput(): Language {
        println(
            """
            Выбор языка (choose language)
            1) RU
            2) ENG
            """.trimIndent()
        )
        return when (val number = numberInput("Язык (Language) (1, 2)", upBound = 2.0).toInt()) {
            1 -> {
                language = Language.RU
                Language.RU
            }
            2 -> {
                language = Language.ENG
                Language.ENG
            }
            else -> throw IllegalArgumentException("Invalid input. Expected 1 or 2, but got $number")
        }
    }

    override fun waysOfSolutionInput(): WayOfSolution {
        println(
            """
            |${StringResource.getStringResource(StringResourceKeys.chooseWaysOfSolution)}
            |${StringResource.getStringResource(StringResourceKeys.chooseWayOneTwoThree)}      
            """.trimMargin("|")
        )
        return when (val number = numberInput(StringResource.getStringResource(StringResourceKeys.choose), upBound = 3.0).toInt()) {
            1 -> WayOfSolution.DEFAULT
            2 -> WayOfSolution.FILL_DATA
            3 -> WayOfSolution.TEST
            else -> throw IllegalArgumentException("Invalid input. Expected 1, 2, or 3, but got $number")
        }
    }

    override fun objectiveFunctionInput(): ObjectiveFunction {
        println(
            """
            |${StringResource.getStringResource(StringResourceKeys.chooseObjectiveFunction)}     
            |${StringResource.getStringResource(StringResourceKeys.objectiveFunctions)}                      
            """.trimMargin("|")
        )
        return when (val number = numberInput(StringResource.getStringResource(StringResourceKeys.choose), upBound = 2.0).toInt()) {
            1 -> ObjectiveFunction.MINIMUM_COST
            2 -> ObjectiveFunction.MAXIMUM_PROFIT
            else -> throw IllegalArgumentException("Invalid input. Expected 1 or 2 but got $number")
        }
    }

    override fun tableSizeInput(): Pair<Int, Int> {
        countOfStocks = numberInput(StringResource.getStringResource(StringResourceKeys.suppliers)).toInt()
        countOfRequired = numberInput(StringResource.getStringResource(StringResourceKeys.consumers)).toInt()
        return Pair(countOfStocks, countOfRequired)
    }

    override fun suppliersTitleInput(): List<String> =
        titleInput(StringResource.getStringResource(StringResourceKeys.inputNameOfSuppliers), countOfStocks)

    override fun consumersTitleInput(): List<String> =
        titleInput(StringResource.getStringResource(StringResourceKeys.inputNameOfConsumers), countOfRequired)

    override fun supplierAvailabilityInput(): List<Double> {
        val result = mutableListOf<Double>()
        println(StringResource.getStringResource(StringResourceKeys.inputAvailabilityOfSuppliers))
        for (i in 0 until countOfStocks) {
            print("${i + 1}) ")
            result.add(numberInput())
        }
        return result
    }

    override fun consumerExpectationInput(): List<Double> {
        val result = mutableListOf<Double>()
        println(StringResource.getStringResource(StringResourceKeys.inputExpectetionOfConsumers))
        for (i in 0 until countOfRequired) {
            print("${i + 1}) ")
            result.add(numberInput())
        }
        return result
    }

    override fun costsOfDeliveryInput(): Matrix<Double> {
        val result: MutableList<MutableList<Double>> = mutableListOf()
        println(StringResource.getStringResource(StringResourceKeys.inputCostOfDelivery))
        for (i in 0 until countOfStocks) {
            val rowList = mutableListOf<Double>()
            for (j in 0 until countOfRequired) {
                print("${StringResource.getStringResource(StringResourceKeys.cell)} ${i + 1}:${j + 1} - ")
                rowList.add(numberInput())
            }
            result.add(rowList)
        }
        return result
    }

    override fun showHistoryOrExitInput(): Boolean {
        println(
            """
            |${StringResource.getStringResource(StringResourceKeys.showHistoryOrExit)}                       
            """.trimMargin("|")
        )
        return when (val number = numberInput(StringResource.getStringResource(StringResourceKeys.choose), upBound = 2.0).toInt()) {
            1 -> true
            2 -> false
            else -> throw IllegalArgumentException("Invalid input. Expected 1 or 2 but got $number")
        }
    }

    private fun titleInput(text: String, count: Int): List<String> {
        val result = mutableListOf<String>()
        println("$text - ")
        repeat(count) {
            print("${it + 1}) ")
            while (true) {
                val title = scanner.next()
                if (title.isBlank())
                    println(StringResource.getStringResource(StringResourceKeys.titleMusntBeNull))
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
        inputRangeError: String = StringResource.getStringResource(StringResourceKeys.numberMustBeInRange),
        inputArgumentError: String = StringResource.getStringResource(StringResourceKeys.inputError),
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