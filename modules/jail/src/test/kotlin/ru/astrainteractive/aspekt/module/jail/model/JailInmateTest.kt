@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.jail.model

import ru.astrainteractive.aspekt.module.jail.usecase.JailTestData
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JailInmateTest {
    private val inmate = JailTestData.inmate

    @Test
    fun GIVEN_inmate_served_longer_than_sentence_WHEN_checked_THEN_sentence_is_over() {
        assertTrue(inmate.isSentenceOver(Instant.ofEpochSecond(11 * 60)))
    }

    @Test
    fun GIVEN_inmate_served_exactly_the_sentence_WHEN_checked_THEN_sentence_is_not_over() {
        assertFalse(inmate.isSentenceOver(Instant.ofEpochSecond(10 * 60)))
    }
}
