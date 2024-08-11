package ru.astrainteractive.aspekt.module.chatgame.store

import ru.astrainteractive.aspekt.module.chatgame.model.ChatGame
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGame.Riddle
import ru.astrainteractive.astralibs.string.StringDesc
import kotlin.random.Random

internal object RiddleGenerator {
    fun generate(instance: ChatGame): ChatGame.Riddle {
        return when (instance) {
            is ChatGame.EquationEasy -> {
                val first = Random.nextInt(0, 99)
                val second = Random.nextInt(0, 99)
                return Riddle(
                    question = StringDesc.Raw(
                        when (Random.nextBoolean()) {
                            true -> "x+$first=$second"
                            false -> "x-$second=-$first"
                        }
                    ),
                    answer = "${second - first}"
                )
            }

            is ChatGame.Riddle -> instance
            is ChatGame.SumOfTwo -> {
                val first = Random.nextInt(0, 200)
                val second = Random.nextInt(0, 200)
                return Riddle(
                    question = StringDesc.Raw("$first+$second"),
                    answer = "${first + second}"
                )
            }

            is ChatGame.TimesOfTwo -> {
                val first = Random.nextInt(0, 30)
                val second = Random.nextInt(0, 30)
                return Riddle(
                    question = StringDesc.Raw("$first*$second"),
                    answer = "${first * second}"
                )
            }
        }
    }
}
