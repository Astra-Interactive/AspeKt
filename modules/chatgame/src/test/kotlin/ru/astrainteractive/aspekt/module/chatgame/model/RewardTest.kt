@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.chatgame.model

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class RewardTest {
    private val drawCount = 1_000

    private fun drawnAmounts(minAmount: Double, maxAmount: Double): Set<Int> {
        val reward = Reward.Money(minAmount = minAmount, maxAmount = maxAmount)
        val random = Random(seed = 42)
        return List(drawCount) { _ -> reward.randomAmount(random) }.toSet()
    }

    @Test
    fun GIVEN_equal_bounds_WHEN_amount_is_drawn_THEN_pays_exactly_that_amount() {
        assertEquals(setOf(50), drawnAmounts(minAmount = 50.0, maxAmount = 50.0))
    }

    @Test
    fun GIVEN_min_below_max_WHEN_amount_is_drawn_THEN_pays_every_amount_from_min_to_max_included() {
        assertEquals((10..15).toSet(), drawnAmounts(minAmount = 10.0, maxAmount = 15.0))
    }

    @Test
    fun GIVEN_min_above_max_WHEN_amount_is_drawn_THEN_pays_every_amount_between_them() {
        assertEquals((10..15).toSet(), drawnAmounts(minAmount = 15.0, maxAmount = 10.0))
    }

    @Test
    fun GIVEN_fractional_bounds_WHEN_amount_is_drawn_THEN_bounds_are_rounded_to_the_nearest_coin() {
        assertEquals((1..3).toSet(), drawnAmounts(minAmount = 1.4, maxAmount = 2.6))
    }

    @Test
    fun GIVEN_negative_bound_WHEN_amount_is_drawn_THEN_it_counts_as_zero() {
        assertEquals((0..2).toSet(), drawnAmounts(minAmount = -5.0, maxAmount = 2.0))
    }
}
