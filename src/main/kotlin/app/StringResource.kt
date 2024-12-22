package app

object StringResource {
    var language: Language = Language.RU

    private val resources: Map<Language, Map<StringResourceKeys, String>> = mapOf(
        Language.RU to mapOf(
            StringResourceKeys.choose to "Выбор",
            StringResourceKeys.chooseWaysOfSolution to "Выбор пути решения",
            StringResourceKeys.chooseWayOneTwoThree to "1) С дефолтными значениями\n2) Задать значения самостоятельно\n3) Тест",
            StringResourceKeys.chooseObjectiveFunction to "Выберете целевую функцию:",
            StringResourceKeys.objectiveFunctions to "1) Минимальные затраты\n2) Максимальная прибыль",
            StringResourceKeys.suppliers to "Поставщики",
            StringResourceKeys.consumers to "Потребители",
            StringResourceKeys.inputNameOfSuppliers to "Введите имена поставщиков",
            StringResourceKeys.inputNameOfConsumers to "Введите имена заказчиков",
            StringResourceKeys.inputAvailabilityOfSuppliers to "Введите запасы поставщиков",
            StringResourceKeys.inputExpectetionOfConsumers to "Введите потребности покупателей",
            StringResourceKeys.inputCostOfDelivery to "Введите стоимость доставки",
            StringResourceKeys.cell to "Ячейка",
            StringResourceKeys.titleMusntBeNull to "Название не должно быть пустым!",
            StringResourceKeys.numberMustBeInRange to "Число должно входить в допустимый диапазон!",
            StringResourceKeys.inputError to "Ошибка ввода. Введите корректное число",
            StringResourceKeys.showHistoryOrExit to "Показать историю изменений плана или выйти? (1, 2)",
            StringResourceKeys.inputData to "Введенные данные",
            StringResourceKeys.table to "Таблица",
            StringResourceKeys.result to "Результат: ",
            StringResourceKeys.cycleStart to "Начало цикла - ",
            StringResourceKeys.cyclePath to "Путь цикла",
            StringResourceKeys.searchOptimalSolution to "Поиск оптимального решения",
            StringResourceKeys.optimalSolutionFound to "Оптимальное решение найдено",
            StringResourceKeys.planAlreadyOptimal to "Найденный план уже является оптимальным",
            StringResourceKeys.historyOfSolution to "История изменения плана",
            StringResourceKeys.lineSeparator to "-+------------------------------------------------------------------+-",
            StringResourceKeys.potentialMatrix to "Матрица потенциалов",
            StringResourceKeys.leastCostMethod to "Метод наименьшей стоимости",
            StringResourceKeys.modiMethod to "Метод потенциалов",
        ),
        Language.ENG to mapOf(
            StringResourceKeys.choose to "Choose",
            StringResourceKeys.chooseWaysOfSolution to "Choose way of solution",
            StringResourceKeys.chooseWayOneTwoThree to "1) Default values\n2) Enter values manually\n3) Test",
            StringResourceKeys.chooseObjectiveFunction to "Choose objective function:",
            StringResourceKeys.objectiveFunctions to "1) Minimal spending\n2) Maximum profit",
            StringResourceKeys.suppliers to "Suppliers",
            StringResourceKeys.consumers to "Consumers",
            StringResourceKeys.inputNameOfSuppliers to "Enter supplier names",
            StringResourceKeys.inputNameOfConsumers to "Enter consumer names",
            StringResourceKeys.inputAvailabilityOfSuppliers to "Enter supplier stock",
            StringResourceKeys.inputExpectetionOfConsumers to "Enter consumer demands",
            StringResourceKeys.inputCostOfDelivery to "Enter delivery costs",
            StringResourceKeys.cell to "Cell",
            StringResourceKeys.titleMusntBeNull to "Title must not be null!",
            StringResourceKeys.numberMustBeInRange to "Number must be within range!",
            StringResourceKeys.inputError to "Input error. Please enter a valid number",
            StringResourceKeys.showHistoryOrExit to "Show history of solution or exit? (1, 2)",
            StringResourceKeys.inputData to "Input data",
            StringResourceKeys.table to "Table",
            StringResourceKeys.result to "Result: ",
            StringResourceKeys.cycleStart to "Cycle start - ",
            StringResourceKeys.cyclePath to "Cycle path",
            StringResourceKeys.searchOptimalSolution to "Searching for optimal solution",
            StringResourceKeys.optimalSolutionFound to "Optimal solution found",
            StringResourceKeys.planAlreadyOptimal to "The found plan is already optimal",
            StringResourceKeys.historyOfSolution to "Solution history",
            StringResourceKeys.lineSeparator to "-+------------------------------------------------------------------+-",
            StringResourceKeys.potentialMatrix to "Potential matrix",
            StringResourceKeys.leastCostMethod to "Least Cost Method",
            StringResourceKeys.modiMethod to "Modi Method",
        )
    )

    fun getStringResource(key: StringResourceKeys): String {
        return resources[language]?.get(key) ?: throw IllegalArgumentException("Resource not found for key: $key")
    }
}
