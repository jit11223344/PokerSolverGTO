package com.example.pokersolverGTO.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// DTOs for persistence
enum class Position { UTG, MP, CO, BTN, SB, BB }

data class CardDTO(val rank: String, val suit: String)

data class GTOStrategyResult(
    val scenario_id: String,
    val position: String,
    val villain_position: String,
    val stack_depth: Int,
    val board: List<String>,
    val action_history: List<String>,
    val hero_range: String?,
    val gto_strategy: Map<String, Float>,
    val range_breakdown: Map<String, List<String>>? = null
)

class DatabaseRepository(private val context: Context) {
    private val gson = Gson()
    private var cache: Map<String, GTOStrategyResult> = emptyMap()

    suspend fun ensureLoaded() = withContext(Dispatchers.IO) {
        if (cache.isNotEmpty()) return@withContext
        val json = context.assets.open("gto_scenarios_sample.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<GTOStrategyResult>>() {}.type
        val scenarios: List<GTOStrategyResult> = gson.fromJson(json, listType)
        cache = scenarios.associateBy { generateKeyRaw(it.position, it.stack_depth, it.board, it.action_history) }
    }

    fun generateKey(position: Position, stackDepth: Int, board: List<CardDTO>, actionHistory: List<String>): String {
        val boardKey = board.joinToString("_") { it.rank + it.suit }
        val actionKey = actionHistory.joinToString("-")
        return "${position.name}_${stackDepth}_${boardKey}_${actionKey}"
    }

    fun generateKeyRaw(position: String, stackDepth: Int, board: List<String>, actionHistory: List<String>): String {
        val boardKey = board.joinToString("_")
        val actionKey = actionHistory.joinToString("-")
        return "${position}_${stackDepth}_${boardKey}_${actionKey}"
    }

    suspend fun queryByKey(key: String): GTOStrategyResult? = withContext(Dispatchers.IO) {
        ensureLoaded()
        cache[key]
    }

    suspend fun findClosestByKey(key: String): GTOStrategyResult? = withContext(Dispatchers.IO) {
        ensureLoaded()
        // simple fallback: find same position and stack depth, ignore board/action
        val tokens = key.split("_")
        if (tokens.size < 2) return@withContext null
        val pos = tokens[0]
        val stack = tokens[1]
        cache.entries.firstOrNull { it.key.startsWith("${pos}_${stack}_") }?.value
    }

    suspend fun getRandomScenario(): GTOStrategyResult? = withContext(Dispatchers.IO) {
        ensureLoaded()
        if (cache.isEmpty()) null else cache.values.random()
    }
}
