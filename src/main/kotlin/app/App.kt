package app

import domain.*
import domain.DataForDefaultWay.createTransportationProblem
import domain.method.LeastCostMethod
import domain.method.ModiMethod
import java.util.*

object App {
    val input: InputHandler = ConsoleInputHandler(scanner = Scanner(System.`in`))
    val output: Output = ConsoleOutput()
    var applicationConfiguration: ApplicationConfiguration? = null
        private set
    var wayOfSolution: WayOfSolution? = null
        private set
    var objectiveFunction: ObjectiveFunction? = null
        private set

    @JvmStatic
    fun main(args: Array<String>) {
        configureApplication()
        chooseWayOfSolution()
        chooseObjectiveFunction()
        startSolution()
    }

    /* solution configuration */
    private fun configureApplication() {
        val language = input.languageInput()

        applicationConfiguration = ApplicationConfiguration(
            language = language,
        )
        StringResource.language = language
    }

    private fun chooseWayOfSolution() {
        val way = input.waysOfSolutionInput()
        wayOfSolution = way
    }

    private fun chooseObjectiveFunction() {
        val function = input.objectiveFunctionInput()
        objectiveFunction = function
    }

    private fun startSolution() {
        when (wayOfSolution) {
            WayOfSolution.DEFAULT -> invokeDefaultWay()
            WayOfSolution.FILL_DATA -> invokeFillDataWay()
            WayOfSolution.TEST -> invokeTestWay()
            null -> throw IllegalArgumentException("Never reach")
        }
    }

    /* Ways of solution */
    private fun invokeDefaultWay() {
        val data = DataForDefaultWay
        val transportationProblem = data.createTransportationProblem()
        transportationProblem.solveProblem()
    }

    private fun invokeFillDataWay() {
        val transportationProblem = getTransportationProblemData()
        transportationProblem.solveProblem()
    }

    private fun invokeTestWay() {
        val data = DataForTestWay
        val transportationProblem = data.createTransportationProblem()
        transportationProblem.solveProblem()
    }

    private fun invokeSecondTestWay() {
        val data = DataForSecondTestWay
        val transportationProblem = data.createTransportationProblem()
        transportationProblem.solveProblem()
    }

    /* utils for ways of solution */
    private fun TransportationProblem.solveProblem() {
        val leastCostMethod = LeastCostMethod(
                config = ConfigurationOfSolution(
                    transportationProblem = this,
                    objectiveFunction = objectiveFunction!!
                )
        )
        val leastCostSolutionResult = leastCostMethod.solution()
        output.showTable(
            methodName = leastCostMethod.name,
            rowHeaders = this.suppliersTitle,
            columnHeaders = this.consumersTitle,
            matrix = leastCostSolutionResult.plan,
            result = leastCostSolutionResult.result,
        )

        val modiMethod = ModiMethod(config = ConfigurationOfSolution(
            transportationProblem = this,
            objectiveFunction = objectiveFunction!!
        )
        )
        val modiSolutionResult = modiMethod.solution()

        output.printFindOptimalPlanMsg()
        when (modiSolutionResult) {
            is OptimalSolutionResult.OptimalPlanWithResult -> {
                output.printOptimalPlanFoundMsg()
                output.showTable(
                    methodName = modiMethod.name,
                    rowHeaders = this.suppliersTitle,
                    columnHeaders = this.consumersTitle,
                    matrix = modiSolutionResult.plan,
                    result = modiSolutionResult.result,
                )
            }
            is OptimalSolutionResult.UnchangedPlanWithResult -> output.printPlanAlreadyOptimalMsg()
        }

        if (input.showHistoryOrExitInput()) {
            output.printHistoryOfSolutionMsg()
            repeat(leastCostMethod.historyOfSolution.size) {
                output.showTable(
                    methodName = leastCostMethod.name,
                    rowHeaders = this.suppliersTitle,
                    columnHeaders = this.consumersTitle,
                    matrix = leastCostMethod.historyOfSolution[it].plan,
                    result = leastCostMethod.historyOfSolution[it].result,
                )
                output.printLine()
            }
            repeat(modiMethod.historyOfPlanWithResultCost.size) {
                output.printPotentialMatrixMsg()
                output.showPotentialMatrix(matrix = modiMethod.historyOfPotentialMatrix[it])
                output.showCycleStartLocationHistory(cell = modiMethod.historyOfCycleInitialLocation[it])
                output.printHistoryOfCycleMsg()
                output.showCycleHistory(listCoordinates = modiMethod.historyOfCyclePath[it])
                output.showTable(
                    rowHeaders = this.suppliersTitle,
                    columnHeaders = this.consumersTitle,
                    matrix = modiMethod.historyOfPlanWithResultCost[it].first,
                    result = modiMethod.historyOfPlanWithResultCost[it].second,
                )
                output.printLine()
            }
        }
    }

    private fun getTransportationProblemData(): TransportationProblem {
        val tableSize = input.tableSizeInput()
        val suppliersTitle = input.suppliersTitleInput()
        val consumersTitle = input.consumersTitleInput()
        val supplierAvailability = input.supplierAvailabilityInput()
        val consumerExpectation = input.consumerExpectationInput()
        val costsOfDelivery = input.costsOfDeliveryInput()

        return TransportationProblemImpl(
            suppliersCount = tableSize.first,
            suppliersTitle = suppliersTitle.toMutableList(),
            suppliersAvailability = supplierAvailability.toMutableList(),
            consumerCount = tableSize.second,
            consumersTitle = consumersTitle.toMutableList(),
            consumersExpectation = consumerExpectation.toMutableList(),
            costsOfDelivery = costsOfDelivery.toMutableMatrix(),
        )
    }
}

