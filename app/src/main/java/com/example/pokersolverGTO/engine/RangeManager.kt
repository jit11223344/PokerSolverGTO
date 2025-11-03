package com.example.pokersolverGTO.engine

import com.google.gson.annotations.SerializedName

// 13x13 grid for 169 combos: rows = ranks high-to-low (A..2), columns = ranks high-to-low (A..2)
// Diagonal = pairs, above diag = suited (e.g., AKs), below = offsuit (e.g., AKo)
class RangeMatrix(
    val matrix: Array<FloatArray> // value in [0f, 100f] frequency
) {
    init {
        require(matrix.size == 13 && matrix.all { it.size == 13 }) { "Range must be 13x13" }
    }
    operator fun get(row: Int, col: Int): Float = matrix[row][col]
    operator fun set(row: Int, col: Int, value: Float) { matrix[row][col] = value.coerceIn(0f, 100f) }
}

// Serializable DTO used for JSON persistence
data class RangeDTO(
    @SerializedName("name") val name: String,
    @SerializedName("matrix") val matrix: List<List<Float>>
) {
    fun toMatrix(): RangeMatrix = RangeMatrix(matrix.map { it.toFloatArray() }.toTypedArray())
    companion object {
        fun fromMatrix(name: String, m: RangeMatrix): RangeDTO = RangeDTO(name, m.matrix.map { it.toList() })
    }
}

object RangeManager {
    // Build an empty range (all 0%)
    fun empty(): RangeMatrix = RangeMatrix(Array(13) { FloatArray(13) { 0f } })

    // Utility to create a simple opening range percentage block, e.g., all pairs >= 99, broadways, suited aces
    fun simpleOpenRange(percent: Float): RangeMatrix {
        val m = empty()
        // naive fill by top-left triangle until approx percent filled
        var filled = 0
        val total = 169
        val target = (total * (percent / 100f)).toInt()
        loop@ for (r in 0 until 13) {
            for (c in 0 until 13) {
                m[r, c] = 100f
                filled++
                if (filled >= target) break@loop
            }
        }
        return m
    }
}
