package lab5.domain

typealias MutableMatrix<T> = MutableList<MutableList<T>>

fun <T> Matrix<T>.toMutableMatrix(): MutableMatrix<T> = this.map { it.toMutableList() }.toMutableList()

fun <T> mutableMatrixOf(columns: Int, rows: Int, vararg value: T): MutableMatrix<T> {
    val matrix = mutableListOf<MutableList<T>>()
    val list = value.toMutableList()

    if (list.size != columns*rows) throw IllegalArgumentException("Size of matrix must be ${columns*rows}, but got ${list.size} elements")

    var start = 0
    var end = rows
    repeat(columns) {
        matrix.add(list.subList(start, end))
        start += rows
        end += rows
    }
    return matrix
}