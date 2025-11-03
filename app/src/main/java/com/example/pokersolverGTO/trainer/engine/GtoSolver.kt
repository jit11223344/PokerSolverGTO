package com.example.pokersolverGTO.trainer.engine

import com.example.pokersolverGTO.engine.HandEvaluator
import com.example.pokersolverGTO.model.*
import com.example.pokersolverGTO.trainer.models.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * GTO Decision Engine - Simplified poker solver for training purposes
 * Uses position-based ranges, board texture analysis, and equity calculations
 */
class GtoSolver {

    fun analyzeDecision(scenario: HandScenario, playerAction: ActionDecision): GTORecommendation {
        return when (scenario.board.street) {
            Street.PREFLOP -> analyzePreflopDecision(scenario, playerAction)
            else -> analyzePostflopDecision(scenario, playerAction)
        }
    }

    private fun analyzePreflopDecision(
        scenario: HandScenario,
        playerAction: ActionDecision
    ): GTORecommendation {
        val hand = scenario.heroHand
        val position = scenario.position

        // Calculate hand strength tier
        val handTier = getPreflopHandTier(hand)

        // Get position-based range
        val shouldPlay = isInOpeningRange(hand, position)
        val shouldRaise = isInRaisingRange(hand, position)

        // Determine optimal action
        val (optimalAction, frequencies) = when {
            scenario.facingBet > 0 -> {
                // Facing a raise
                val should3Bet = isIn3BetRange(hand, position)
                val shouldCall = isInCallingRange(hand, position, scenario.facingBet, scenario.potSize)

                when {
                    should3Bet -> PokerAction.RAISE to mapOf(
                        PokerAction.RAISE to 0.8f,
                        PokerAction.CALL to 0.15f,
                        PokerAction.FOLD to 0.05f
                    )
                    shouldCall -> PokerAction.CALL to mapOf(
                        PokerAction.CALL to 0.7f,
                        PokerAction.FOLD to 0.2f,
                        PokerAction.RAISE to 0.1f
                    )
                    else -> PokerAction.FOLD to mapOf(
                        PokerAction.FOLD to 0.9f,
                        PokerAction.CALL to 0.1f
                    )
                }
            }
            else -> {
                // Opening action
                when {
                    shouldRaise -> PokerAction.RAISE to mapOf(
                        PokerAction.RAISE to 0.85f,
                        PokerAction.CALL to 0.10f,
                        PokerAction.FOLD to 0.05f
                    )
                    shouldPlay -> PokerAction.CALL to mapOf(
                        PokerAction.CALL to 0.6f,
                        PokerAction.RAISE to 0.3f,
                        PokerAction.FOLD to 0.1f
                    )
                    else -> PokerAction.FOLD to mapOf(
                        PokerAction.FOLD to 0.95f,
                        PokerAction.CALL to 0.05f
                    )
                }
            }
        }

        // Calculate EV difference
        val playerFreq = frequencies[playerAction.action] ?: 0.01f
        val optimalFreq = frequencies[optimalAction] ?: 1.0f
        val evLoss = calculateEVLoss(playerFreq, optimalFreq, handTier)

        // Generate explanation
        val explanation = buildPreflopExplanation(
            hand, position, optimalAction, playerAction.action, scenario.facingBet
        )

        return GTORecommendation(
            optimalAction = optimalAction,
            actionFrequencies = frequencies,
            evDifference = evLoss,
            explanation = explanation,
            rangeAdvice = getRangeAdvice(hand, position)
        )
    }

    private fun analyzePostflopDecision(
        scenario: HandScenario,
        playerAction: ActionDecision
    ): GTORecommendation {
        val board = scenario.board
        val hand = scenario.heroHand

        // Evaluate hand strength
        val allCards = listOf(hand.card1, hand.card2) + board.cards
        val handStrength = HandEvaluator.evaluateHand(allCards)

        // Analyze board texture
        val boardTexture = analyzeBoardTexture(board.cards)

        // Calculate approximate equity (simplified)
        val equity = estimateEquity(hand, board.cards, handStrength)

        // Pot odds calculation
        val potOdds = if (scenario.facingBet > 0) {
            scenario.facingBet.toFloat() / (scenario.potSize + scenario.facingBet)
        } else 0f

        // Determine optimal action based on equity, position, and board
        val (optimalAction, frequencies) = determinePostflopAction(
            equity, potOdds, scenario.position, boardTexture, scenario.facingBet
        )

        val playerFreq = frequencies[playerAction.action] ?: 0.01f
        val optimalFreq = frequencies[optimalAction] ?: 1.0f
        val evLoss = calculatePostflopEVLoss(
            playerFreq, optimalFreq, equity, scenario.potSize, scenario.facingBet
        )

        val explanation = buildPostflopExplanation(
            handStrength, equity, potOdds, boardTexture, optimalAction, playerAction.action
        )

        return GTORecommendation(
            optimalAction = optimalAction,
            actionFrequencies = frequencies,
            evDifference = evLoss,
            explanation = explanation,
            rangeAdvice = "Your hand: ${handStrength.description}\nEquity: ${(equity * 100).toInt()}%"
        )
    }

    // Preflop range logic
    private fun getPreflopHandTier(hand: PokerHand): Int {
        val r1 = hand.card1.rank.value
        val r2 = hand.card2.rank.value
        val isPair = r1 == r2
        val isSuited = hand.card1.suit == hand.card2.suit
        val highCard = maxOf(r1, r2)
        val lowCard = minOf(r1, r2)
        val gap = highCard - lowCard

        return when {
            // Premium pairs
            isPair && highCard >= Rank.TEN.value -> 1
            // High broadway cards
            highCard >= Rank.ACE.value && lowCard >= Rank.KING.value -> 1
            // Suited broadway
            isSuited && highCard >= Rank.KING.value && lowCard >= Rank.TEN.value -> 2
            // Medium pairs
            isPair && highCard >= Rank.SEVEN.value -> 2
            // Suited connectors
            isSuited && gap <= 1 && highCard >= Rank.NINE.value -> 3
            // Any suited ace
            isSuited && highCard == Rank.ACE.value -> 3
            // Broadway cards
            highCard >= Rank.JACK.value && lowCard >= Rank.TEN.value -> 4
            // Small pairs
            isPair -> 4
            // Trash
            else -> 5
        }
    }

    private fun isInOpeningRange(hand: PokerHand, position: PokerPosition): Boolean {
        val tier = getPreflopHandTier(hand)
        return when (position) {
            PokerPosition.UTG, PokerPosition.MP -> tier <= 2
            PokerPosition.CO -> tier <= 3
            PokerPosition.BTN -> tier <= 4
            PokerPosition.SB, PokerPosition.BB -> tier <= 3
        }
    }

    private fun isInRaisingRange(hand: PokerHand, position: PokerPosition): Boolean {
        val tier = getPreflopHandTier(hand)
        return when (position) {
            PokerPosition.UTG, PokerPosition.MP -> tier == 1
            PokerPosition.CO -> tier <= 2
            PokerPosition.BTN -> tier <= 3
            PokerPosition.SB, PokerPosition.BB -> tier <= 2
        }
    }

    private fun isIn3BetRange(hand: PokerHand, position: PokerPosition): Boolean {
        val tier = getPreflopHandTier(hand)
        return tier == 1 || (tier == 2 && position in listOf(PokerPosition.BTN, PokerPosition.SB))
    }

    private fun isInCallingRange(
        hand: PokerHand,
        position: PokerPosition,
        facingBet: Int,
        potSize: Int
    ): Boolean {
        val tier = getPreflopHandTier(hand)
        val potOdds = facingBet.toFloat() / (potSize + facingBet)
        return tier <= 3 && potOdds < 0.3f
    }

    // Postflop analysis
    private fun estimateEquity(
        hand: PokerHand,
        board: List<Card>,
        handStrength: HandEvaluation
    ): Float {
        // Simplified equity estimation based on hand rank
        return when (handStrength.handRank.value) {
            10, 9 -> 0.95f + Random.nextFloat() * 0.05f // Royal/Straight flush
            8 -> 0.85f + Random.nextFloat() * 0.10f // Quads
            7 -> 0.75f + Random.nextFloat() * 0.15f // Full house
            6 -> 0.65f + Random.nextFloat() * 0.15f // Flush
            5 -> 0.55f + Random.nextFloat() * 0.15f // Straight
            4 -> 0.45f + Random.nextFloat() * 0.20f // Trips
            3 -> 0.35f + Random.nextFloat() * 0.20f // Two pair
            2 -> 0.25f + Random.nextFloat() * 0.25f // Pair
            else -> 0.10f + Random.nextFloat() * 0.20f // High card
        }
    }

    private fun analyzeBoardTexture(cards: List<Card>): BoardTexture {
        if (cards.size < 3) return BoardTexture.DRY

        val ranks = cards.map { it.rank.value }.sorted()
        val suits = cards.groupBy { it.suit }

        val isPaired = ranks.size < cards.size
        val flushDraw = suits.any { it.value.size >= 2 }
        val straightDraw = ranks.zipWithNext().any { (a, b) -> b - a <= 2 }

        return when {
            isPaired -> BoardTexture.PAIRED
            flushDraw -> BoardTexture.WET
            straightDraw -> BoardTexture.CONNECTED
            else -> BoardTexture.DRY
        }
    }

    private fun determinePostflopAction(
        equity: Float,
        potOdds: Float,
        position: PokerPosition,
        texture: BoardTexture,
        facingBet: Int
    ): Pair<PokerAction, Map<PokerAction, Float>> {
        return when {
            facingBet > 0 -> {
                // Facing a bet - decision tree
                when {
                    equity > 0.7f -> PokerAction.RAISE to mapOf(
                        PokerAction.RAISE to 0.7f,
                        PokerAction.CALL to 0.25f,
                        PokerAction.FOLD to 0.05f
                    )
                    equity > potOdds + 0.1f -> PokerAction.CALL to mapOf(
                        PokerAction.CALL to 0.7f,
                        PokerAction.RAISE to 0.2f,
                        PokerAction.FOLD to 0.1f
                    )
                    equity > potOdds - 0.05f && texture == BoardTexture.WET -> PokerAction.CALL to mapOf(
                        PokerAction.CALL to 0.5f,
                        PokerAction.FOLD to 0.4f,
                        PokerAction.RAISE to 0.1f
                    )
                    else -> PokerAction.FOLD to mapOf(
                        PokerAction.FOLD to 0.8f,
                        PokerAction.CALL to 0.2f
                    )
                }
            }
            else -> {
                // No bet facing - betting decision
                when {
                    equity > 0.6f -> PokerAction.RAISE to mapOf(
                        PokerAction.RAISE to 0.65f,
                        PokerAction.CHECK to 0.35f
                    )
                    equity > 0.4f && position in listOf(PokerPosition.BTN, PokerPosition.CO) ->
                        PokerAction.RAISE to mapOf(
                            PokerAction.RAISE to 0.4f,
                            PokerAction.CHECK to 0.6f
                        )
                    else -> PokerAction.CHECK to mapOf(
                        PokerAction.CHECK to 0.7f,
                        PokerAction.RAISE to 0.3f
                    )
                }
            }
        }
    }

    // EV calculations
    private fun calculateEVLoss(playerFreq: Float, optimalFreq: Float, handTier: Int): Float {
        val baseEV = when (handTier) {
            1 -> 2.0f
            2 -> 1.0f
            3 -> 0.5f
            4 -> 0.2f
            else -> 0.1f
        }
        return baseEV * (optimalFreq - playerFreq)
    }

    private fun calculatePostflopEVLoss(
        playerFreq: Float,
        optimalFreq: Float,
        equity: Float,
        potSize: Int,
        facingBet: Int
    ): Float {
        val totalPot = potSize + facingBet
        return totalPot * equity * (optimalFreq - playerFreq) / 100f
    }

    // Explanations
    private fun buildPreflopExplanation(
        hand: PokerHand,
        position: PokerPosition,
        optimal: PokerAction,
        player: PokerAction,
        facingBet: Int
    ): String {
        val tier = getPreflopHandTier(hand)
        val handDesc = when (tier) {
            1 -> "premium hand"
            2 -> "strong hand"
            3 -> "playable hand"
            4 -> "marginal hand"
            else -> "weak hand"
        }

        val positionDesc = when (position) {
            PokerPosition.UTG, PokerPosition.MP -> "early position"
            PokerPosition.CO -> "middle position"
            PokerPosition.BTN -> "button"
            PokerPosition.SB -> "small blind"
            PokerPosition.BB -> "big blind"
        }

        return when {
            optimal == player -> "✓ Correct! ${hand.toNotation()} is a $handDesc from $positionDesc. " +
                    "Your $optimal action is GTO optimal."
            facingBet > 0 -> "✗ With ${hand.toNotation()} facing a raise from $positionDesc, " +
                    "$optimal is preferred over $player. This is a $handDesc that should be played ${optimal.name.lowercase()}."
            else -> "✗ ${hand.toNotation()} from $positionDesc is a $handDesc. " +
                    "GTO recommends $optimal instead of $player to maximize EV."
        }
    }

    private fun buildPostflopExplanation(
        handStrength: HandEvaluation,
        equity: Float,
        potOdds: Float,
        texture: BoardTexture,
        optimal: PokerAction,
        player: PokerAction
    ): String {
        val equityPercent = (equity * 100).toInt()
        val oddsPercent = (potOdds * 100).toInt()

        return when {
            optimal == player -> "✓ Correct! ${handStrength.description} with $equityPercent% equity on this ${texture.name.lowercase()} board. " +
                    "Your $optimal action is optimal."
            potOdds > 0 -> "✗ You have ${handStrength.description} ($equityPercent% equity) facing pot odds of $oddsPercent%. " +
                    "On this ${texture.name.lowercase()} board, $optimal is better than $player."
            else -> "✗ ${handStrength.description} ($equityPercent% equity) on a ${texture.name.lowercase()} board. " +
                    "$optimal maximizes EV compared to $player."
        }
    }

    private fun getRangeAdvice(hand: PokerHand, position: PokerPosition): String {
        val tier = getPreflopHandTier(hand)
        return when (position) {
            PokerPosition.UTG, PokerPosition.MP ->
                "UTG/MP range: Top ${if (tier <= 2) "15" else "30"}% of hands. Play tight, premium hands only."
            PokerPosition.CO ->
                "CO range: Top ${if (tier <= 3) "25" else "40"}% of hands. Wider than early position."
            PokerPosition.BTN ->
                "BTN range: Top ${if (tier <= 4) "45" else "60"}% of hands. Widest opening range."
            PokerPosition.SB, PokerPosition.BB ->
                "Blind range: Defend vs steals with ${if (tier <= 3) "good" else "marginal"} hands."
        }
    }
}

enum class BoardTexture {
    DRY, WET, CONNECTED, PAIRED
}
