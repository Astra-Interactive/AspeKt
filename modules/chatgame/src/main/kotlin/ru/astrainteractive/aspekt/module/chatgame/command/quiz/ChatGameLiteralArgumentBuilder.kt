package ru.astrainteractive.aspekt.module.chatgame.command.quiz

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.model.Reward
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStore
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.coroutines.launch
import kotlin.random.Random

/**
 * ChatGame /quiz command registrar. Preserves legacy behavior:
 * - Player-only
 * - With or without an answer argument (no-args uses empty string)
 * - Checks active game, validates answer with mutex to ensure single winner
 * - Rewards money if configured and ends the game
 */
@Suppress("LongParameterList")
internal class ChatGameLiteralArgumentBuilder(
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val chatGameStore: ChatGameStore,
    private val chatGameConfig: ChatGameConfig,
    private val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory,
    private val ioScope: CoroutineScope,
    private val multiplatformCommand: MultiplatformCommand
) {
    private val translation by translationKrate
    private val kyori by kyoriKrate
    private val mutex = Mutex()

    private fun handleAnswer(player: OnlineKPlayer, answer: String) {
        ioScope.launch(mutex) {
            supervisorScope {
                val chatGame = chatGameStore.state.first() as? ChatGameStore.State.Started
                val reward = chatGame?.chatGame?.reward ?: chatGameConfig.defaultReward
                if (chatGame == null) {
                    player.sendMessage(kyori.toComponent(translation.chatGame.noQuizAvailable))
                    return@supervisorScope
                }
                if (!chatGameStore.isAnswerCorrect(answer)) {
                    player.sendMessage(kyori.toComponent(translation.chatGame.wrongAnswer))
                    return@supervisorScope
                } else {
                    when (reward) {
                        is Reward.Money -> {
                            val amount = Random.nextInt(reward.minAmount.toInt(), reward.maxAmount.toInt())
                            val economy = when (val currencyId = reward.currencyId) {
                                null -> currencyEconomyProviderFactory.findDefault()
                                else -> currencyEconomyProviderFactory.findByCurrencyId(currencyId)
                            }
                            economy?.addMoney(player.uuid, amount.toDouble())
                            translation.chatGame.gameEndedMoneyReward(
                                player.name,
                                amount
                            ).let(kyori::toComponent).run(Bukkit::broadcast)
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
                runs { ctx ->
                    val player = ctx.requirePlayer()
                    handleAnswer(player, "")
                }
                argument("answer", StringArgumentType.greedyString()) { answerArg ->
                    runs { ctx ->
                        val player = ctx.requirePlayer()
                        val answer = ctx.requireArgument(answerArg)
                        handleAnswer(player, answer)
                    }
                }
            }
        }
    }
}
