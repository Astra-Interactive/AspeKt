package ru.astrainteractive.aspekt.module.chatgame.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.aspekt.module.chatgame.api.ChatGameStore
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.model.Reward
import ru.astrainteractive.aspekt.module.chatgame.service.MoneyRewardPayer
import ru.astrainteractive.aspekt.module.chatgame.service.broadcast
import ru.astrainteractive.aspekt.plugin.model.PluginTranslation
import ru.astrainteractive.aspekt.plugin.util.moneyRewarded
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.coroutines.launch

@Suppress("LongParameterList")
internal class ChatGameLiteralArgumentBuilder(
    translationKrate: CachedKrate<PluginTranslation>,
    private val chatGameStore: ChatGameStore,
    chatGameConfigKrate: CachedKrate<ChatGameConfig>,
    private val moneyRewardPayer: MoneyRewardPayer,
    private val ioScope: CoroutineScope,
    private val multiplatformCommand: MultiplatformCommand,
    private val commandExceptionHandler: CommandExceptionHandler
) {
    private val translation by translationKrate
    private val chatGameConfig by chatGameConfigKrate
    private val mutex = Mutex()

    private fun checkAnswer(player: OnlineKPlayer, answer: String) {
        ioScope.launch(mutex) {
            supervisorScope {
                val chatGame = chatGameStore.state.first() as? ChatGameStore.State.Started
                val reward = chatGame?.chatGame?.reward ?: chatGameConfig.defaultReward
                if (chatGame == null) {
                    player.sendMessage(translation.chatGame.noQuizAvailable)
                    return@supervisorScope
                }
                if (!chatGameStore.isAnswerCorrect(answer)) {
                    player.sendMessage(translation.chatGame.wrongAnswer)
                    return@supervisorScope
                } else {
                    when (reward) {
                        is Reward.Money -> {
                            val amount = moneyRewardPayer.pay(player.uuid, reward)
                            if (amount == null) {
                                player.sendMessage(translation.chatGame.rewardNotPaid)
                            } else {
                                Bukkit.getServer().broadcast(translation.chatGame.moneyRewarded(player.name, amount))
                            }
                        }
                    }
                    chatGameStore.endCurrentGame()
                    return@supervisorScope
                }
            }
        }
    }

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("quiz") {
                runs(commandExceptionHandler::onException) { ctx ->
                    val player = ctx.requirePlayer()
                    checkAnswer(player, "")
                }
                argument("answer", StringArgumentType.greedyString()) { answerArg ->
                    runs(commandExceptionHandler::onException) { ctx ->
                        val player = ctx.requirePlayer()
                        val answer = ctx.requireArgument(answerArg)
                        checkAnswer(player, answer)
                    }
                }
            }
        }
    }
}
