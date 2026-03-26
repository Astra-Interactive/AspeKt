package ru.astrainteractive.aspekt.module.moneyadvancement.event

import io.papermc.paper.advancement.AdvancementDisplay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.module.moneyadvancement.model.MoneyAdvancementsConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

class MoneyAdvancementEvent(
    mAdvConfigKrate: CachedKrate<MoneyAdvancementsConfiguration>,
    private val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<PluginTranslation>
) : EventListener,
    Logger by JUtiltLogger("MoneyAdvancementEvent"),
    CoroutineFeature by CoroutineFeature.Default(Dispatchers.IO).withTimings(),
    KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val mAdvConfig by mAdvConfigKrate
    private val translation by translationKrate

    @EventHandler
    fun onAdvancement(e: PlayerAdvancementDoneEvent) {
        val economy = when (val currencyId = mAdvConfig.currencyId) {
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
                val amount = mAdvConfig.challenge.toDouble()
                launch { economy.addMoney(e.player.uniqueId, amount) }
                e.player.sendMessage(translation.moneyAdvancement.challengeCompleted(amount).component)
            }

            AdvancementDisplay.Frame.GOAL -> {
                val amount = mAdvConfig.goal.toDouble()
                launch { economy.addMoney(e.player.uniqueId, amount) }
                e.player.sendMessage(translation.moneyAdvancement.goalCompleted(amount).component)
            }

            AdvancementDisplay.Frame.TASK -> {
                val amount = mAdvConfig.task.toDouble()
                launch { economy.addMoney(e.player.uniqueId, amount) }
                e.player.sendMessage(translation.moneyAdvancement.taskCompleted(amount).component)
            }
        }
    }
}
