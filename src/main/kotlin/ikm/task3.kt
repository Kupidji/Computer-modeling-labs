package ikm

import kotlin.math.pow

fun main() {
    val lambda = 1.5
    val mu = 2.0
    val m = 2
    val channels = listOf(2, 3, 4, 5)

    analyzeSingleChannelRejection(lambda, mu)
    analyzeSingleChannelLimitedQueue(lambda, mu, m)
    analyzeSingleChannelUnlimitedQueue(lambda, mu)

    for (n in channels) {
        analyzeMultichannelRejection(lambda, mu, n)
    }

    for (n in channels) {
        analyzeMultichannelLimitedQueue(lambda, mu, n, m)
    }

    for (n in channels) {
        analyzeMultichannelUnlimitedQueue(lambda, mu, n)
    }
}

private fun analyzeSingleChannelRejection(lambda: Double, mu: Double) {
    println("Анализ одноканальной СМО с отказами:\n")
    val rho = lambda / mu
    val P0 = 1 / (1 + rho)
    val P1 = rho * P0
    val Q = P0
    val A = lambda * Q

    println("Нагрузка системы (ρ) = ${"%.5f".format(rho)}")
    println("Вероятность простоя (P0) = ${"%.5f".format(P0)}")
    println("Вероятность занятости (P1) = ${"%.5f".format(P1)}")
    println("Вероятность отказа (Pотк) = ${"%.5f".format(P1)}")
    println("Относительная пропускная способность (Q) = ${"%.5f".format(Q)}")
    println("Абсолютная пропускная способность (A) = ${"%.5f".format(A)} авто/час")
}

private fun analyzeSingleChannelLimitedQueue(lambda: Double, mu: Double, m: Int) {
    println("\nАнализ одноканальной СМО с ограниченной очередью:\n")
    val rho = lambda / mu
    val P = DoubleArray(m + 2)
    P[0] = (1 - rho) / (1 - rho.pow(m + 2))
    for (k in 1..m + 1) {
        P[k] = rho.pow(k) * P[0]
    }

    val P_reject = P[m + 1]
    val Q = 1 - P_reject
    val A = lambda * Q
    val numerator = rho.pow(2) * (1 - rho.pow(m) * (m - m * rho + 1))
    val denominator = (1 - rho).pow(2)
    val L_queue = numerator / denominator * P[0]
    val T_queue = L_queue / lambda
    val L_serve = (m + 1).toDouble() / (m + 2)
    val L_system = L_serve + L_queue
    val T_system = L_system / lambda

    println("Нагрузка системы (ρ) = ${"%.5f".format(rho)}")
    println("Вероятность простоя (P0) = ${"%.5f".format(P[0])}")
    println("Вероятность отказа (Pотк) = ${"%.5f".format(P_reject)}")
    println("Вероятности состояний (P) = ${P.joinToString { "%.5f".format(it) }}")
    println("Сумма вероятностей состояний (P) = ${"%.5f".format(P.sum())}")
    println("Относительная пропускная способность (Q) = ${"%.5f".format(Q)}")
    println("Абсолютная пропускная способность (A) = ${"%.5f".format(A)} авто/час")
    println("Средняя длина очереди (Lоч) = ${"%.5f".format(L_queue)}")
    println("Среднее время ожидания (Tоч) = ${"%.5f".format(T_queue)} час")
    println("Среднее число на обслуживании (Lоб) = ${"%.5f".format(L_serve)}")
    println("Среднее число в системе (Lсмо) = ${"%.5f".format(L_system)}")
    println("Среднее время в системе (Tсмо) = ${"%.5f".format(T_system)} час")
}

private fun analyzeSingleChannelUnlimitedQueue(lambda: Double, mu: Double) {
    println("\nАнализ одноканальной СМО с неограниченной очередью:\n")
    val rho = lambda / mu

    val Q = 1.0
    val A = lambda
    val P_reject = 0.0
    val P0 = 1 - rho
    val P = List(4) { k -> if (k == 0) P0 else rho.pow(k) * P0 }

    val L_queue = rho.pow(2) / (1 - rho)
    val L_system = rho / (1 - rho)
    val T_queue = L_queue / lambda
    val T_system = T_queue + 1 / mu

    println("Нагрузка системы (ρ) = ${"%.5f".format(rho)}")
    println("Вероятность простоя (P0) = ${"%.5f".format(P0)}")
    println("Вероятность отказа (Pотк) = ${"%.5f".format(P_reject)}")
    println("Вероятности состояний для 4 заявок (P) = ${P.joinToString { "%.5f".format(it) }}")
    println("Относительная пропускная способность (Q) = ${"%.5f".format(Q)}")
    println("Абсолютная пропускная способность (A) = ${"%.5f".format(A)} авто/час")
    println("Средняя длина очереди (Lоч) = ${"%.5f".format(L_queue)}")
    println("Среднее время ожидания (Tоч) = ${"%.5f".format(T_queue)} час")
    println("Среднее число в системе (Lсмо) = ${"%.5f".format(L_system)}")
    println("Среднее время в системе (Tсмо) = ${"%.5f".format(T_system)} час")
}

private fun analyzeMultichannelRejection(lambda: Double, mu: Double, n: Int) {
    println("\nАнализ многоканальной СМО с отказами:\n")
    val rho = lambda / mu

    val sumTerm = (0..n).sumOf { k -> rho.pow(k) / factorial(k) }
    val P0 = 1 / sumTerm

    val P = DoubleArray(n + 1)
    P[0] = P0
    for (k in 1..n) {
        P[k] = (rho.pow(k) / factorial(k)) * P0
    }

    val PReject = (rho.pow(n) / factorial(n)) * P0
    val Q = 1 - PReject
    val avgBusyChannels = rho * Q
    val A = avgBusyChannels * mu

    println("-- Кол-во каналов: $n")
    println("Нагрузка системы (ρ) = ${"%.5f".format(rho)}")
    println("Вероятность простоя (P0) = ${"%.5f".format(P0)}")
    println("Вероятность отказа (Pотк) = ${"%.5f".format(PReject)}")
    println("Вероятности состояний (P) = ${P.joinToString { "%.5f".format(it) }}")
    println("Сумма вероятностей состояний (P) = ${"%.5f".format(P.sum())}")
    println("Вероятность обслуживания (Pобсл) = ${"%.5f".format(Q)}")
    println("Относительная пропускная способность (Q) = ${"%.5f".format(Q)}")
    println("Абсолютная пропускная способность (A) = ${"%.5f".format(A)} авто/час")
    println("Среднее число занятых каналов = ${"%.5f".format(avgBusyChannels)}")
}

private data class Result(
    val stateProbabilities: List<Double>,
    val systemLoad: Double,
    val idleProbability: Double,
    val rejectProbability: Double,
    val throughput: Double,
    val absoluteThroughput: Double,
    val avgBusyChannels: Double,
    val avgQueueLength: Double?,
    val avgQueueTime: Double?,
    val avgSystemTime: Double?,
)

private fun analyzeMultichannelLimitedQueue(lambda: Double, mu: Double, n: Int, m: Int): Result {
    println("\nАнализ многоканальной СМО с ограниченной очередью:\n")
    val rho = lambda / mu

    val sum1 = (0..n).sumOf { k -> rho.pow(k) / factorial(k) }
    val sum2 = (rho.pow(n + 1)) / (factorial(n) * (n - rho)) * (1 - (rho / n).pow(m))
    val P0 = 1 / (sum1 + sum2)

    val P = DoubleArray(n + m + 1)
    P[0] = P0
    for (k in 1..n) {
        P[k] = (rho.pow(k) / factorial(k)) * P0
    }
    for (k in n + 1 until n + m + 1) {
        P[k] = (rho.pow(k) / (factorial(n) * n.toDouble().pow(k - n))) * P0
    }

    val PReject = P[n + m]
    val Q = 1 - PReject
    val A = lambda * Q

    val term1 = rho.pow(n + 1) / (n * factorial(n))
    val rhoN = rho / n
    val numerator = 1 - (rhoN).pow(m) * (m + 1 - m * rhoN)
    val denominator = (1 - rhoN).pow(2)
    val avgQueueLength = term1 * (numerator / denominator) * P0

    val avgQueueTime = avgQueueLength / lambda
    val avgBusyChannels = rho * Q
    val avgSystemTime = avgQueueTime + Q / mu

    println("-- Кол-во каналов: $n, длина очереди: $m")
    println("Нагрузка системы (ρ) = ${"%.5f".format(rho)}")
    println("Приведенная нагрузка (ρ/n) = ${"%.5f".format(rhoN)}")
    println("Вероятность простоя (P0) = ${"%.5f".format(P0)}")
    println("Вероятность отказа (Pотк) = ${"%.5f".format(PReject)}")
    println("Вероятности состояний (P) = ${P.joinToString { "%.5f".format(it) }}")
    println("Сумма вероятностей состояний (P) = ${"%.5f".format(P.sum())}")
    println("Относительная пропускная способность (Q) = ${"%.5f".format(Q)}")
    println("Абсолютная пропускная способность (A) = ${"%.5f".format(A)} авто/час")
    println("Средняя длина очереди (Lоч) = ${"%.5f".format(avgQueueLength)}")
    println("Среднее время ожидания (Tоч) = ${"%.5f".format(avgQueueTime)} час")
    println("Среднее число занятых каналов = ${"%.5f".format(avgBusyChannels)}")
    println("Среднее время пребывания в системе (Tсмо) = ${"%.5f".format(avgSystemTime)} час")

    return Result(P.toList(), rho, P0, PReject, Q, A, avgBusyChannels, avgQueueLength, avgQueueTime, avgSystemTime)
}

private fun analyzeMultichannelUnlimitedQueue(lambda: Double, mu: Double, n: Int): Result {
    println("\nАнализ многоканальной СМО с неограниченной очередью:\n")
    val rho = lambda / mu
    val rhoN = rho / n

    val sum1 = (0..n).sumOf { k -> rho.pow(k) / factorial(k) }
    val sum2 = (rho.pow(n + 1)) / (factorial(n) * (n - rho))
    val P0 = 1 / (sum1 + sum2)

    val PQueue = ((rho.pow(n + 1)) / (factorial(n) * (n - rho))) * P0

    val P = mutableListOf(P0)
    P.addAll((1..3).map { k -> (rho.pow(k) / factorial(k)) * P0 })

    val avgQueueLength = (n / (n - rho)) * PQueue
    val avgQueueTime = avgQueueLength / lambda
    val avgSystemTime = avgQueueTime + 1 / mu

    println("-- Кол-во каналов: $n")
    println("Нагрузка системы (ρ) = ${"%.5f".format(rho)}")
    println("Приведенная нагрузка (ρ/n) = ${"%.5f".format(rhoN)}")
    println("Вероятность простоя (P0) = ${"%.5f".format(P0)}")
    println("Вероятность образования очереди (Pоч) = ${"%.5f".format(PQueue)}")
    println("Вероятности состояний для 4 заявок (P) = ${P.joinToString { "%.5f".format(it) }}")
    println("Средняя длина очереди (Lоч) = ${"%.5f".format(avgQueueLength)}")
    println("Среднее время ожидания (Tоч) = ${"%.5f".format(avgQueueTime)} час")
    println("Среднее время пребывания в системе (Tсмо) = ${"%.5f".format(avgSystemTime)} час")

    return Result(P, rho, P0, PQueue, 1.0, lambda, rho, avgQueueLength, avgQueueTime, avgSystemTime)
}

private fun factorial(n: Int): Double = if (n == 0) 1.0 else (1..n).fold(1.0) { acc, i -> acc * i }