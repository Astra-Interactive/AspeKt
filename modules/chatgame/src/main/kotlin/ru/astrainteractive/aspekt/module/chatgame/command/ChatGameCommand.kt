package ru.astrainteractive.aspekt.module.chatgame.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.module.chatgame.model.ChatGameConfig
import ru.astrainteractive.aspekt.module.chatgame.model.Reward
import ru.astrainteractive.aspekt.module.chatgame.store.ChatGameStore
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import kotlin.random.Random

@Suppress("LongParameterList")
internal class ChatGameCommand(
    private val plugin: JavaPlugin,
    private val chatGameStore: ChatGameStore,
    kyoriComponentSerializerProvider: CachedKrate<KyoriComponentSerializer>,
    translationProvider: CachedKrate<PluginTranslation>,
    chatGameConfigProvider: CachedKrate<ChatGameConfig>,
    private val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory,
    private val scope: CoroutineScope
) : Logger by JUtiltLogger("ChatGameCommand"),
    KyoriComponentSerializer by kyoriComponentSerializerProvider.unwrap() {
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
                player.sendMessage(translation.chatGame.noQuizAvailable.component)
                return@setExecutor true
            }
            EntityType.BAT
            scope.launch {
                supervisorScope {
                    mutex.withLock {
                        if (!chatGameStore.isAnswerCorrect(answer)) {
                            player.sendMessage(translation.chatGame.wrongAnswer.component)
                            return@withLock
                        } else {
                            when (val reward = reward) {
                                is Reward.Money -> {
                                    val amount = Random.nextInt(reward.minAmount.toInt(), reward.maxAmount.toInt())
                                    val economy = when (val currencyId = reward.currencyId) {
                                        null -> currencyEconomyProviderFactory.findDefault()
                                        else -> currencyEconomyProviderFactory.findByCurrencyId(currencyId)
                                    }
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
            true
        }
    }
}
