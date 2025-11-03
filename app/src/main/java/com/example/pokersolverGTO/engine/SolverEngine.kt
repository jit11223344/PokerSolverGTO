package com.example.pokersolverGTO.engine

import android.content.Context
import com.example.pokersolverGTO.repository.CardDTO
import com.example.pokersolverGTO.repository.DatabaseRepository
import com.example.pokersolverGTO.repository.GTOStrategyResult
import com.example.pokersolverGTO.repository.Position

class SolverEngine(private val repo: DatabaseRepository) {

    suspend fun getGTOStrategy(
        position: Position,
        stackDepth: Int,
        board: List<CardDTO>,
        actionHistory: List<String>,
        heroRange: RangeMatrix?
    ): GTOStrategyResult? {
        val key = repo.generateKey(position, stackDepth, board, actionHistory)
        return repo.queryByKey(key) ?: repo.findClosestByKey(key)
    }

    suspend fun calculateEV(action: String, equityPercent: Float, potSize: Int, betSize: Int): Float {
        // Simple EV approximation for demonstration
        val equity = equityPercent / 100f
        return when (action) {
            "check" -> equity * potSize
            "bet" -> equity * (potSize + betSize) - (1 - equity) * betSize
            "fold" -> 0f
            else -> 0f
        }
    }

    companion object {
        fun create(context: Context): SolverEngine = SolverEngine(DatabaseRepository(context))
    }
}
