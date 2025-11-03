package com.example.pokersolverGTO.repository

// Additional models for UI mapping

data class StrategyActionFrequency(
    val action: String,
    val frequency: Float
)

fun GTOStrategyResult.toActionFrequencies(): List<StrategyActionFrequency> =
    gto_strategy.entries.map { StrategyActionFrequency(it.key, it.value) }.sortedByDescending { it.frequency }

