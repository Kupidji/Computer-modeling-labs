package lab5.domain

class Cell(
    val coordinates: Coordinates,
    val value: Any,
) {
    override fun toString(): String = "cell($coordinates, $value)"
}