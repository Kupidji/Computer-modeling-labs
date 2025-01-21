package lab5.domain.method.utils

import lab5.domain.TransportationProblem

interface TransportationProblemCloser {
    /**
     * Balance problem if transportation problem is open
     * @return Close transportation problem
     * * Problem is open if sum of suppliers availability != sum of consumers expectation
     */
    fun TransportationProblem.checkForOpenAndCloseProblem() {
        fun checkForOpen(): Boolean = suppliersAvailability.sum() != consumersExpectation.sum()

        /**
         * Adding a dummy producer or consumer if problem is open
         */
        fun balanceProblem() {
            val totalStock = suppliersAvailability.sum()
            val totalRequired = consumersExpectation.sum()

            when {
                totalStock > totalRequired -> {
                    consumerCount++
                    consumersTitle.add("C$consumerCount")
                    consumersExpectation.add(totalStock - totalRequired)
                    for (row in costsOfDelivery) {
                        row.add(0.0)
                    }
                }
                totalRequired > totalStock -> {
                    suppliersCount++
                    suppliersTitle.add("S$suppliersCount")
                    suppliersAvailability.add(totalRequired - totalStock)
                    val newRow = MutableList(consumersExpectation.size) { 0.0 }
                    costsOfDelivery.add(newRow)
                }

                else -> { throw IllegalStateException("checkWeights is false but true was expected") }
            }
        }

        if (checkForOpen()) balanceProblem()
    }
}