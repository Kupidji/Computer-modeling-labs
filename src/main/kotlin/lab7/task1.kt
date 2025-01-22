package lab7

import kotlin.math.exp
import kotlin.random.Random

fun main() {
    val durationWork = 480  // продолжительность работы в минутах
    val numExperiments = 50  // количество экспериментов
    val timeObsl = 108  // среднее время обслуживания в минутах

    val postResults = mutableListOf<Int>()
    val obslResults = mutableListOf<Int>()
    val otkResults = mutableListOf<Int>()

    fun rnPost(): Int {
        return Random.nextInt(1, 61)
    }

    fun poisson(lambda: Int): Int {
        val L = exp((-lambda).toDouble())
        var p = 1.0
        var k = 0
        do {
            k++
            p *= Random.nextDouble()
        } while (p > L)
        return k - 1
    }

    fun modelingSMO(durTime: Int): Triple<Int, Int, Int> {
        var post = 0
        var obsl = 0
        var otk = 0
        var tOkon = 0

        for (t in 0 until durTime) {
            if (rnPost() == 1) {
                post++
                if (tOkon == 0) {
                    obsl++
                    tOkon = poisson(timeObsl)
                } else {
                    otk++
                }
            }
            if (tOkon > 0) {
                tOkon--
            }
        }
        return Triple(post, obsl, otk)
    }

    for (i in 0 until numExperiments) {
        val (post, obsl, otk) = modelingSMO(durationWork)
        postResults.add(post)
        obslResults.add(obsl)
        otkResults.add(otk)
    }

    val postMean = postResults.average()
    val obslMean = obslResults.average()
    val otkMean = otkResults.average()

    println("Среднее количество заявок: $postMean")
    println("Среднее количество обслуженных заявок: $obslMean")
    println("Среднее количество отклоненных заявок: $otkMean")
}