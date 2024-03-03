package com.tafreshiali.imageprocessing.state

data class RGBMatrix(
    val red: Array<IntArray>,
    val green: Array<IntArray>,
    val blue: Array<IntArray>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RGBMatrix

        if (!red.contentDeepEquals(other.red)) return false
        if (!green.contentDeepEquals(other.green)) return false
        return blue.contentDeepEquals(other.blue)
    }

    override fun hashCode(): Int {
        var result = red.contentDeepHashCode()
        result = 31 * result + green.contentDeepHashCode()
        result = 31 * result + blue.contentDeepHashCode()
        return result
    }
}