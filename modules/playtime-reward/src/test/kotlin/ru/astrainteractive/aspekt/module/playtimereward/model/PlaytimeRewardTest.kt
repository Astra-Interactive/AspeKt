@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.playtimereward.model

import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class PlaytimeRewardTest {
    private val joinedAt = Instant.fromEpochSeconds(0)
    private val reward = PlaytimeReward(
        timePlayed = 10.minutes,
        uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
        latestUsername = "Player",
        latestJoin = joinedAt
    )

    @Test
    fun GIVEN_session_WHEN_counted_THEN_session_time_is_added_and_session_restarts() {
        val now = joinedAt + 15.minutes

        val counted = reward.countedUntil(now)

        assertEquals(25.minutes, counted.timePlayed)
        assertEquals(now, counted.latestJoin)
    }

    @Test
    fun GIVEN_join_WHEN_recorded_THEN_only_the_join_moment_changes() {
        val now = joinedAt + 5.minutes

        val joined = reward.joinedAt(now)

        assertEquals(10.minutes, joined.timePlayed)
        assertEquals(now, joined.latestJoin)
    }

    @Test
    fun GIVEN_required_duration_WHEN_checked_THEN_reward_is_due_from_that_duration() {
        assertTrue(reward.isRewardDue(10.minutes))
        assertFalse(reward.isRewardDue(11.minutes))
    }
}
