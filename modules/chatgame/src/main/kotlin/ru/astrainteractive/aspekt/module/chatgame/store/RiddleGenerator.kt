package ru.astrainteractive.aspekt.module.chatgame.store

import ru.astrainteractive.aspekt.module.chatgame.model.ChatGame
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameData
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.klibs.kstorage.api.Krate
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt
import kotlin.random.Random

internal class RiddleGenerator(
    configProvider: Krate<ChatGameConfig>,
    translationProvider: Krate<PluginTranslation>
) {
    private val config by configProvider
    private val translation by translationProvider

    @Suppress("LongMethod", "CyclomaticComplexMethod")
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
                    answers = listOf("${second - first}"),
                    reward = instance.reward ?: config.defaultReward
                )
            }

            is ChatGame.Riddle -> ChatGameData(
                question = translation.chatGame.solveRiddle(instance.question.raw),
                answers = listOf(instance.answer),
                reward = instance.reward ?: config.defaultReward
            )

            is ChatGame.SumOfTwo -> {
                val first = Random.nextInt(0, 200)
                val second = Random.nextInt(0, 200)
                return ChatGameData(
                    question = translation.chatGame.solveExample("$first+$second"),
                    answers = listOf("${first + second}"),
                    reward = instance.reward ?: config.defaultReward
                )
            }

            is ChatGame.TimesOfTwo -> {
                val first = Random.nextInt(0, 30)
                val second = Random.nextInt(0, 30)
                return ChatGameData(
                    question = translation.chatGame.solveExample("$first*$second"),
                    answers = listOf("${first * second}"),
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
                    answers = listOf(word),
                    reward = instance.reward ?: config.defaultReward
                )
            }

            is ChatGame.QuadraticEquation -> {
                val nonZeroRandom = ((-12..12).toList() - 0)
                val b = nonZeroRandom.random()
                val a = nonZeroRandom.random()
                val c = nonZeroRandom.random().let { c ->
                    if (a < 0) c * c.sign else -1
                }
                val equation = buildString {
                    append(a)
                    append("x²")
                    if (a != 0 && b.sign == 1) append("+")
                    append(b)
                    append("x")
                    if (c.sign == 1 && (a != 0 || b != 0)) append("+")
                    append(c)
                }

                fun Double.roundDec(dec: Int): Double {
                    val tens = 10.0.pow(dec)
                    return kotlin.math.round(this * tens) / tens
                }

                val d = b * b - 4 * a * c
                val sqrtD = sqrt(d.toDouble())
                val root1 = (-b + sqrtD).div(2 * a).roundDec(2)
                val root2 = (-b - sqrtD).div(2 * a).roundDec(2)
                ChatGameData(
                    question = translation.chatGame.solveQuadratic(equation),
                    answers = listOf(root1.toString(), root2.toString()).also { println(it) },
                    reward = instance.reward ?: config.defaultReward
                )
            }
        }
    }
}
