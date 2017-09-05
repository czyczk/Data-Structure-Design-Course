package model

data class Route(var name1: String, var name2: String, var distance: Double) {
    init {
        if (distance < 0) throw IllegalArgumentException()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Route

        if (distance != other.distance) return false
        if ((name1 == other.name1 || name2 == other.name2) && (name1 == other.name2 && name2 == other.name1))
            return true
        return false
    }
}