package ru.astrainteractive.aspekt.module.chatgame.store.generator

import ru.astrainteractive.aspekt.module.chatgame.model.ChatGame
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameData
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.klibs.kstorage.api.Krate
import kotlin.random.Random

internal class AnagramRiddleGenerator(
    configKrate: Krate<ChatGameConfig>,
    translationKrate: Krate<PluginTranslation>
) {
    private val config by configKrate
    private val translation by translationKrate

    private var lastAnagramIndex = Random.nextInt(0, Int.MAX_VALUE)
        get() {
            if (field >= Int.MAX_VALUE) field = 0
            return field
        }

    fun generate(instance: ChatGame.Anagram): ChatGameData {
        val word = instance.words[lastAnagramIndex % instance.words.size]
        val anagram: String = buildString {
            word.indices.shuffled().forEach { i ->
                this.append(word[i])
            }
        }
        return ChatGameData(
            question = translation.chatGame.solveAnagram(anagram),
            answers = listOf(word),
            reward = instance.reward ?: config.defaultReward
        )
    }
}
