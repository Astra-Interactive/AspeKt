package ru.astrainteractive.aspekt.module.moneyadvancement.event

import io.papermc.paper.advancement.AdvancementDisplay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

class MoneyAdvancementEvent(
    configurationProvider: CachedKrate<PluginConfiguration>,
    private val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory,
    kyoriComponentSerializerProvider: CachedKrate<KyoriComponentSerializer>,
    translationProvider: CachedKrate<PluginTranslation>
) : EventListener,
    Logger by JUtiltLogger("MoneyAdvancementEvent"),
    CoroutineFeature by CoroutineFeature.Default(Dispatchers.IO),
    KyoriComponentSerializer by kyoriComponentSerializerProvider.unwrap() {
    private val configuration by configurationProvider
    private val translation by translationProvider

    @EventHandler
    fun onAdvancement(e: PlayerAdvancementDoneEvent) {
        val economy = when (val currencyId = configuration.advancementMoney.currencyId) {
            null -> currencyEconomyProviderFactory.findDefault()
            else -> currencyEconomyProviderFactory.findByCurrencyId(currencyId)
        } ?: run {
            error { "#onAdvancement economy not found" }
            return
        }
        val frame = e.advancement.display?.frame() ?: return
        info { "#onAdvancement ${e.advancement.key} ${e.advancement.display?.title()}" }
        when (frame) {
            AdvancementDisplay.Frame.CHALLENGE -> {
                val amount = configuration.advancementMoney.challenge.toDouble()
                launch { economy.addMoney(e.player.uniqueId, amount) }
                e.player.sendMessage(translation.moneyAdvancement.challengeCompleted(amount).component)
            }

            AdvancementDisplay.Frame.GOAL -> {
                val amount = configuration.advancementMoney.goal.toDouble()
                launch { economy.addMoney(e.player.uniqueId, amount) }
                e.player.sendMessage(translation.moneyAdvancement.goalCompleted(amount).component)
            }

            AdvancementDisplay.Frame.TASK -> {
                val amount = configuration.advancementMoney.task.toDouble()
                launch { economy.addMoney(e.player.uniqueId, amount) }
                e.player.sendMessage(translation.moneyAdvancement.taskCompleted(amount).component)
            }
        }
    }
}
