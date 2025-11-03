package com.example.pokersolverGTO.navigation

import com.example.pokersolverGTO.trainer.models.TrainingMode

sealed class Screen(val route: String) {
    object Trainer : Screen("trainer")
    object Preflop : Screen("preflop")
    object Postflop : Screen("postflop")
    object HandRanking : Screen("hand_ranking")
    object BestHand : Screen("best_hand")
    object PotOdds : Screen("pot_odds")
    object Table : Screen("poker_table")
    object GTOScenario : Screen("gto_scenario")
    object EquityCalculator : Screen("equity_calculator")

    // New Trainer Pro screens
    object TrainerHome : Screen("trainer_home")
    object TrainerMode : Screen("trainer_mode/{mode}") {
        fun createRoute(mode: TrainingMode) = "trainer_mode/${mode.name}"
    }
    object StatsDetail : Screen("stats_detail")
    object Settings : Screen("settings")
}
