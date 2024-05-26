package ru.astrainteractive.aspekt.module.antiswear.util

import net.kyori.adventure.text.Component
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SwearRuRegexTest {
    @Test
    fun GIVEN_swear_WHEN_regex_THEN_match() {
        assertTrue(SwearRuRegex.SWEAR_REGEX.matches("бля"))
        assertTrue(SwearRuRegex.SWEAR_REGEX.matches("еблан"))
        assertFalse(SwearRuRegex.SWEAR_REGEX.containsMatchIn("Привет как дела"))
        assertTrue(SwearRuRegex.SWEAR_REGEX.containsMatchIn("Привет блять бля"))
    }

    @Test
    fun GIVEN_component_with_swears_WHEN_replace_by_regex_THEN_contains_replacement() {
        val component = Component.text("бля урод ебаный гандон")
        val replaced = component.replaceText(SwearRuRegex.REPLACEMENT_CONFIG)
        assertTrue(replaced.toString().contains(SwearRuRegex.REPLACEMENT_STRING))
    }
}
