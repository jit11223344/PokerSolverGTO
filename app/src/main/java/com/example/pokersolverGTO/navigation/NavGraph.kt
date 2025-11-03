package com.example.pokersolverGTO.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokersolverGTO.ui.screens.TrainerScreen
import com.example.pokersolverGTO.ui.screens.exercises.*
import com.example.pokersolverGTO.ui.screens.PokerTableScreen
import com.example.pokersolverGTO.ui.screens.GTOScenarioScreen
import com.example.pokersolverGTO.ui.screens.EquityCalculatorScreen
import com.example.pokersolverGTO.ui.screens.SettingsScreen
import com.example.pokersolverGTO.trainer.models.TrainingMode
import com.example.pokersolverGTO.trainer.ui.TrainerHomeScreen
import com.example.pokersolverGTO.trainer.ui.PokerTrainingTableScreen
import com.example.pokersolverGTO.trainer.ui.StatsDetailScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.TrainerHome.route) {
        // Original trainer exercises
        composable(Screen.Trainer.route) {
            TrainerScreen(onNavigateToExercise = { navController.navigate(it) })
        }
        composable(Screen.Preflop.route) {
            PreflopTrainerScreen(onBackPressed = { navController.popBackStack() })
        }
        composable(Screen.Postflop.route) {
            PostflopTrainerScreen(onBackPressed = { navController.popBackStack() })
        }
        composable(Screen.HandRanking.route) {
            HandRankingQuizScreen(onBackPressed = { navController.popBackStack() })
        }
        composable(Screen.BestHand.route) {
            BestHandIdentifierScreen(onBackPressed = { navController.popBackStack() })
        }
        composable(Screen.PotOdds.route) {
            PotOddsCalculatorScreen(onBackPressed = { navController.popBackStack() })
        }
        composable(Screen.Table.route) {
            PokerTableScreen(onBackPressed = { navController.popBackStack() })
        }
        composable(Screen.GTOScenario.route) {
            GTOScenarioScreen(onBackPressed = { navController.popBackStack() })
        }
        composable(Screen.EquityCalculator.route) {
            EquityCalculatorScreen(onBackPressed = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBackPressed = { navController.popBackStack() })
        }

        // New Trainer Pro screens
        composable(Screen.TrainerHome.route) {
            TrainerHomeScreen(
                onModeSelected = { mode ->
                    navController.navigate(Screen.TrainerMode.createRoute(mode))
                },
                onViewStats = {
                    navController.navigate(Screen.StatsDetail.route)
                }
            )
        }

        composable(
            route = Screen.TrainerMode.route,
            arguments = listOf(navArgument("mode") { type = NavType.StringType })
        ) { backStackEntry ->
            val modeString = backStackEntry.arguments?.getString("mode") ?: "PREFLOP"
            val mode = TrainingMode.valueOf(modeString)
            PokerTrainingTableScreen(
                onBackPressed = { navController.popBackStack() },
                mode = mode
            )
        }

        composable(Screen.StatsDetail.route) {
            StatsDetailScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}
