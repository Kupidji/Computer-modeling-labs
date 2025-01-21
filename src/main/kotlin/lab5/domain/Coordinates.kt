package lab5.domain

class Coordinates(
    val x: Int,
    val y: Int,
) {
    override fun toString(): String = "coorditates: x = $x, y = $y"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true // Сравнение по ссылке
        if (other !is Coordinates) return false // Проверка типа

        return x == other.x && y == other.y
    }

    override fun hashCode(): Int = 31 * x + y
}