package ru.astrainteractive.aspekt.module.antiswear.util

import net.kyori.adventure.text.Component
import org.junit.Test

class SwearRuRegexTest {
    @Test
    fun GIVEN_swear_WHEN_regex_THEN_match() {
        assert(SwearRuRegex.SWEAR_REGEX.matches("бля"))
        assert(SwearRuRegex.SWEAR_REGEX.matches("еблан"))
    }

    @Test
    fun GIVEN_component_with_swears_WHEN_replace_by_regex_THEN_contains_replacement() {
        val component = Component.text("бля урод ебаный гандон")
        val replaced = component.replaceText(SwearRuRegex.REPLACEMENT_CONFIG)
        assert(replaced.toString().contains(SwearRuRegex.REPLACEMENT_STRING))
    }
}
