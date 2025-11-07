package ru.astrainteractive.aspekt.module.chatgame.command.quiz

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.model.Reward
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStore
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.util.argument
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.requireArgument
import ru.astrainteractive.astralibs.command.api.util.requirePlayer
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.coroutines.launch
import kotlin.random.Random

/**
 * ChatGame /quiz command registrar. Preserves legacy behavior:
 * - Player-only
 * - With or without an answer argument (no-args uses empty string)
 * - Checks active game, validates answer with mutex to ensure single winner
 * - Rewards money if configured and ends the game
 */
internal class ChatGameCommandRegistrar(
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val chatGameStore: ChatGameStore,
    private val chatGameConfig: ChatGameConfig,
    private val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory,
    private val ioScope: CoroutineScope
) {
    private val translation by translationKrate
    private val kyori by kyoriKrate
    private val mutex = Mutex()

    private fun handleAnswer(player: Player, answer: String) {
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
                            economy?.addMoney(player.uniqueId, amount.toDouble())
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

    fun createNode(): LiteralCommandNode<CommandSourceStack> {
        return command("quiz") {
            runs { ctx ->
                val player: Player = ctx.requirePlayer()
                handleAnswer(player, "")
            }
            argument("answer", StringArgumentType.greedyString()) { answerArg ->
                runs { ctx ->
                    val player: Player = ctx.requirePlayer()
                    val answer = ctx.requireArgument(answerArg)
                    handleAnswer(player, answer)
                }
            }
        }.build()
    }
}
