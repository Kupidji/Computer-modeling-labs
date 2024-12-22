package domain

typealias MutableMatrix<T> = MutableList<MutableList<T>>

fun <T> Matrix<T>.toMutableMatrix(): MutableMatrix<T> = this.map { it.toMutableList() }.toMutableList()

fun <T> mutableMatrixOf(columns: Int, rows: Int, vararg value: T): MutableMatrix<T> {
    val matrix = mutableListOf<MutableList<T>>()
    val list = value.toMutableList()

    if (list.size != columns*rows) throw IllegalArgumentException("Размер матрицы должен быть ${columns*rows}, но введено лишь ${list.size} элементов")

    var start = 0
    var end = rows
    repeat(columns) {
        matrix.add(list.subList(start, end))
        start += rows
        end += rows
    }
    return matrix
}