package domain

interface TransportationProblem {
    var suppliersCount: Int
    val suppliersTitle: MutableList<String>
    val suppliersAvailability: MutableList<Double>
    var consumerCount: Int
    val consumersTitle: MutableList<String>
    val consumersExpectation: MutableList<Double>
    val costsOfDelivery: MutableMatrix<Double>
    var plan: MutableMatrix<Any>?
}

class TransportationProblemImpl(
    override var suppliersCount: Int,
    override var suppliersTitle: MutableList<String>,
    override val suppliersAvailability: MutableList<Double>,
    override var consumerCount: Int,
    override val consumersTitle: MutableList<String>,
    override val consumersExpectation: MutableList<Double>,
    override val costsOfDelivery: MutableMatrix<Double>,
    override var plan: MutableMatrix<Any>? = null
) : TransportationProblem