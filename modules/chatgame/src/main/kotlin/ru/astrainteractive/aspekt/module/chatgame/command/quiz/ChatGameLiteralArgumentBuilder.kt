package ru.astrainteractive.aspekt.module.chatgame.command.quiz

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.core.command.CommandExceptionHandler
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.model.Reward
import ru.astrainteractive.aspekt.module.chatgame.service.MoneyRewardPayer
import ru.astrainteractive.aspekt.module.chatgame.service.broadcast
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStore
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.coroutines.launch

/**
 * ChatGame /quiz command registrar. Preserves legacy behavior:
 * - Player-only
 * - With or without an answer argument (no-args uses empty string)
 * - Checks active game, validates answer with mutex to ensure single winner
 * - Rewards money if configured and ends the game; a reward that could not be paid is not announced
 */
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

    private fun handleAnswer(player: OnlineKPlayer, answer: String) {
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
                runs(commandExceptionHandler::handle) { ctx ->
                    val player = ctx.requirePlayer()
                    handleAnswer(player, "")
                }
                argument("answer", StringArgumentType.greedyString()) { answerArg ->
                    runs(commandExceptionHandler::handle) { ctx ->
                        val player = ctx.requirePlayer()
                        val answer = ctx.requireArgument(answerArg)
                        handleAnswer(player, answer)
                    }
                }
            }
        }
    }
}
