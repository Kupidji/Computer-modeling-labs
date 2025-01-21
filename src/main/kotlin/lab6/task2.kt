package lab6

import kotlin.random.Random

fun main() {
    val lambdaRate = 17.0 / 1200.0
    val serviceMean = 65.0
    val queueLimit = 3
    val experimentTime = 5000
    val numExperiments = 50

    val results = mutableListOf<List<Int>>()

    repeat(numExperiments) {
        var tOch1 = 0
        var tOch2 = 0
        var tOch3 = 0
        var smTObs = 0
        var post = 0
        var otk = 0
        var obsl = 0
        var queue = 0
        var serviceEndTime = 0

        for (t in 1..experimentTime) {
            val t1 = Random.nextInt(1, 1201)
            if (t1 in 1..17) {
                post++
                if (queue < queueLimit) {
                    queue++
                } else {
                    otk++
                }
            }

            if (serviceEndTime <= t && queue > 0) {
                queue--
                obsl++
                val tObsl = (-serviceMean * Math.log(Random.nextDouble())).toInt()
                serviceEndTime = t + tObsl
                smTObs += tObsl
            }

            when (queue) {
                1 -> tOch1++
                2 -> tOch2++
                3 -> tOch3++
            }
        }

        results.add(listOf(tOch1, tOch2, tOch3, smTObs, post, otk, obsl))
    }

    val summary = results[0].indices.map { index ->
        results.sumOf { it[index] }.toDouble() / numExperiments
    }

    val columns = listOf("t_och1", "t_och2", "t_och3", "sm_t_obs", "post", "otk", "obsl")
    columns.zip(summary).forEach { (column, value) ->
        println("$column: ${"%.2f".format(value)}")
    }
}
