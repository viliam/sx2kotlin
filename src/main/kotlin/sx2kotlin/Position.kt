package sx2kotlin

data class Position(val x: Int, val y: Int) {
    fun addX(by: Int): Position = Position(x + by, y)
    override fun toString(): String = "($x,$y)"

    companion object {
        fun create(pos: Position): Position = Position(pos.x, pos.y)
    }
}
