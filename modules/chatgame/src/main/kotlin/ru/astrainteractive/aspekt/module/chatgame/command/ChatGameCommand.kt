package ru.astrainteractive.aspekt.module.chatgame.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.model.Reward
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStore
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import kotlin.random.Random

@Suppress("LongParameterList")
internal class ChatGameCommand(
    private val plugin: JavaPlugin,
    private val chatGameStore: ChatGameStore,
    kyoriComponentSerializerProvider: Provider<KyoriComponentSerializer>,
    translationProvider: Provider<PluginTranslation>,
    economyProvider: Provider<EconomyProvider?>,
    chatGameConfigProvider: Provider<ChatGameConfig>,
    private val scope: CoroutineScope
) : Logger by JUtiltLogger("ChatGameCommand") {
    private val economy by economyProvider
    private val kyoriComponentSerializer by kyoriComponentSerializerProvider
    private val translation by translationProvider
    private val chatGameConfig by chatGameConfigProvider
    private val mutex = Mutex()

    fun register() {
        val command = plugin.getCommand("quiz") ?: run {
            error { "#register command quiz not found!" }
            return
        }
        command.setExecutor { commandSender, _, _, strings ->
            val player = commandSender as? Player ?: return@setExecutor true
            val answer = strings.joinToString(" ")
            val chatGame = chatGameStore.state.value as? ChatGameStore.State.Started
            val reward = chatGame?.chatGame?.reward ?: chatGameConfig.defaultReward
            if (chatGame == null) {
                with(kyoriComponentSerializer) {
                    player.sendMessage(translation.chatGame.noQuizAvailable.component)
                    return@setExecutor true
                }
            }
            scope.launch {
                supervisorScope {
                    mutex.withLock {
                        if (!chatGameStore.isAnswerCorrect(answer)) {
                            with(kyoriComponentSerializer) {
                                player.sendMessage(translation.chatGame.wrongAnswer.component)
                                return@withLock
                            }
                        } else {
                            with(kyoriComponentSerializer) {
                                when (val reward = reward) {
                                    is Reward.Money -> {
                                        val amount = Random.nextInt(reward.minAmount.toInt(), reward.maxAmount.toInt())
                                        economy?.addMoney(player.uniqueId, amount.toDouble())
                                        Bukkit.broadcast(
                                            translation.chatGame.gameEndedMoneyReward(
                                                player.name,
                                                amount
                                            ).component
                                        )
                                    }
                                }
                                chatGameStore.endCurrentGame()
                                return@withLock
                            }
                        }
                    }
                }
            }
            true
        }
    }
}
