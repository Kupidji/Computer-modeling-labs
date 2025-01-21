package lab5.domain

interface DataForWay {
    val tableSize: Pair<Int, Int>
    val suppliersTitle: MutableList<String>
    val consumersTitle: MutableList<String>
    val supplierValues: MutableList<Double>
    val consumerValues: MutableList<Double>
    val costsOfDelivery: Matrix<Double>
    val plan: Matrix<Any>?
    fun DataForWay.createTransportationProblem(): TransportationProblem
}

object DataForDefaultWay: DataForWay {
    override val tableSize = Pair(4, 5)
    override val suppliersTitle = mutableListOf("Мрамор", "Малахит", "Проволка", "Пластмасса")
    override val consumersTitle = mutableListOf("А", "Б", "В", "Г", "Д")
    override val supplierValues = mutableListOf(17.0, 33.0, 15.0, 20.0)
    override val consumerValues = mutableListOf(10.0, 20.0, 15.0, 15.0, 20.0)
    override val costsOfDelivery = matrixOf(
        tableSize.first,
        tableSize.second,
        90.0, 190.0, 140.0, 170.0, 90.0,
        40.0, 210.0, 270.0, 80.0, 290.0,
        122.0, 230.0, 40.0, 15.0, 240.0,
        100.0, 220.0, 80.0, 75.0, 270.0,
    ).transformTable()
    override val plan: Matrix<Any>? = null

    private fun Matrix<Double>.transformTable(): List<List<Double>> {
        val maxProfit = this.flatten().maxOrNull() ?: 0.0

        return this.map { row ->
            row.map { profit ->
                maxProfit - profit
            }
        }
    }

    override fun DataForWay.createTransportationProblem(): TransportationProblem =
        TransportationProblemImpl(
            suppliersCount = tableSize.first,
            suppliersTitle = suppliersTitle,
            suppliersAvailability = supplierValues.toMutableList(),
            consumerCount = tableSize.second,
            consumersTitle = consumersTitle,
            consumersExpectation = consumerValues.toMutableList(),
            costsOfDelivery = costsOfDelivery.toMutableMatrix(),
        )
}

object DataForTestWay: DataForWay {
    override val tableSize = Pair(3, 4)
    override val suppliersTitle = mutableListOf("1", "2", "3")
    override val consumersTitle = mutableListOf("А", "Б", "В", "Г")
    override val supplierValues = mutableListOf(160.0, 140.0, 170.0)
    override val consumerValues = mutableListOf(120.0, 50.0, 190.0, 110.0)
    override val costsOfDelivery = matrixOf(
        tableSize.first,
        tableSize.second,
        7.0, 8.0, 1.0, 2.0,
        4.0, 5.0, 9.0, 8.0,
        9.0, 2.0, 3.0, 6.0,
    )
    override val plan: Matrix<Any>? = null

    override fun DataForWay.createTransportationProblem(): TransportationProblem =
        TransportationProblemImpl(
            suppliersCount = tableSize.first,
            suppliersTitle = suppliersTitle,
            suppliersAvailability = supplierValues.toMutableList(),
            consumerCount = tableSize.second,
            consumersTitle = consumersTitle,
            consumersExpectation = consumerValues.toMutableList(),
            costsOfDelivery = costsOfDelivery.toMutableMatrix(),
        )
}

object DataForSecondTestWay: DataForWay {
    override val tableSize = Pair(4, 4)
    override val suppliersTitle = mutableListOf("1", "2", "3", "4")
    override val consumersTitle = mutableListOf("А", "Б", "В", "Г")
    override val supplierValues = mutableListOf(35.0, 70.0, 65.0, 30.0)
    override val consumerValues = mutableListOf(40.0, 85.0, 25.0, 50.0)
    override val costsOfDelivery = mutableMatrixOf(
        tableSize.first,
        tableSize.second,
        9.0, 3.0, 6.0, 5.0,
        4.0, 10.0, 11.0, 8.0,
        2.0, 2.0, 3.0, 9.0,
        3.0, 4.0, 9.0, 12.0,
    )
    override val plan = matrixOf(
        tableSize.first,
        tableSize.second,
        "-", 20.0, "-", 15.0,
        10.0, "-", 25.0, 35.0,
        "-", 65.0, "-", "-",
        30.0, "-", "-", "-",
    )

    override fun DataForWay.createTransportationProblem(): TransportationProblem =
        TransportationProblemImpl(
            suppliersCount = tableSize.first,
            suppliersTitle = suppliersTitle,
            suppliersAvailability = supplierValues.toMutableList(),
            consumerCount = tableSize.second,
            consumersTitle = consumersTitle,
            consumersExpectation = consumerValues.toMutableList(),
            costsOfDelivery = costsOfDelivery.toMutableMatrix(),
            plan = plan!!.toMutableMatrix(),
        )
}

