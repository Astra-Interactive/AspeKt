package ru.astrainteractive.aspekt.module.chatgame.store

import kotlin.random.Random
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGame
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameData
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

internal class RiddleGenerator(
    configProvider: Provider<ChatGameConfig>,
    translationProvider: Provider<PluginTranslation>
) {
    private val config by configProvider
    private val translation by translationProvider

    fun generate(instance: ChatGame): ChatGameData {
        return when (instance) {
            is ChatGame.EquationEasy -> {
                val first = Random.nextInt(0, 99)
                val second = Random.nextInt(0, 99)
                return ChatGameData(
                    question = translation.chatGame.solveExample(
                        when (Random.nextBoolean()) {
                            true -> "x+$first=$second"
                            false -> "x-$second=-$first"
                        }
                    ),
                    answer = "${second - first}",
                    reward = instance.reward ?: config.defaultReward
                )
            }

            is ChatGame.Riddle -> ChatGameData(
                question = translation.chatGame.solveRiddle(instance.question.raw),
                answer = instance.answer,
                reward = instance.reward ?: config.defaultReward
            )

            is ChatGame.SumOfTwo -> {
                val first = Random.nextInt(0, 200)
                val second = Random.nextInt(0, 200)
                return ChatGameData(
                    question = translation.chatGame.solveExample("$first+$second"),
                    answer = "${first + second}",
                    reward = instance.reward ?: config.defaultReward
                )
            }

            is ChatGame.TimesOfTwo -> {
                val first = Random.nextInt(0, 30)
                val second = Random.nextInt(0, 30)
                return ChatGameData(
                    question = translation.chatGame.solveExample("$first*$second"),
                    answer = "${first * second}",
                    reward = instance.reward ?: config.defaultReward
                )
            }

            is ChatGame.Anagram -> {
                val word = instance.words.random()
                val anagram: String = buildString {
                    word.indices.shuffled().forEach { i ->
                        this.append(word[i])
                    }
                }
                ChatGameData(
                    question = translation.chatGame.solveAnagram(anagram),
                    answer = word,
                    reward = instance.reward ?: config.defaultReward
                )
            }
        }
    }
}
