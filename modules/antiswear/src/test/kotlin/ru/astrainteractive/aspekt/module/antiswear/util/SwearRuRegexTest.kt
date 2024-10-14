package ru.astrainteractive.aspekt.module.antiswear.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.junit.Test
import kotlin.test.assertEquals

class SwearRuRegexTest {
    private fun Component.toPlain(): String {
        return PlainTextComponentSerializer.plainText().serialize(this)
    }

    @Test
    fun GIVEN_swear_WHEN_regex_THEN_match() {
        assert(SwearRuRegex.SWEAR_REGEX.matches("бля"))
        assert(SwearRuRegex.SWEAR_REGEX.matches("еблан"))
        assert(!SwearRuRegex.SWEAR_REGEX.matches("Привет друг"))
    }

    @Test
    fun GIVEN_component_with_swears_WHEN_replace_by_regex_THEN_contains_replacement() {
        val component = Component.text("бля урод ебаный гандон")
        val replaced = component.replaceText(SwearRuRegex.REPLACEMENT_CONFIG)
        assert(replaced.toString().contains(SwearRuRegex.REPLACEMENT_STRING))
    }

    @Test
    @Suppress("MaxLineLength", "MaximumLineLength")
    fun GIVEN_component_with_swears_caps_WHEN_replace_by_regex_THEN_contains_replacement() {
        val component = Component.text("бля БЛЯ ХУЙ хуй")
        val replaced = component.replaceText(SwearRuRegex.REPLACEMENT_CONFIG)
        assert(replaced.toString().contains(SwearRuRegex.REPLACEMENT_STRING))
        val expect = "${SwearRuRegex.REPLACEMENT_STRING} ${SwearRuRegex.REPLACEMENT_STRING} ${SwearRuRegex.REPLACEMENT_STRING} ${SwearRuRegex.REPLACEMENT_STRING}"
        assertEquals(expect, replaced.toPlain())
    }
}
