package ru.astrainteractive.aspekt.module.moneyadvancement.event

import io.papermc.paper.advancement.AdvancementDisplay
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import ru.astrainteractive.aspekt.economy.api.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.module.moneyadvancement.model.MoneyAdvancementsConfiguration
import ru.astrainteractive.aspekt.plugin.model.PluginTranslation
import ru.astrainteractive.aspekt.plugin.util.challengeCompleted
import ru.astrainteractive.aspekt.plugin.util.goalCompleted
import ru.astrainteractive.aspekt.plugin.util.taskCompleted
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.server.util.asKAudience
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

internal class MoneyAdvancementEvent(
    mAdvConfigKrate: CachedKrate<MoneyAdvancementsConfiguration>,
    private val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory,
    translationKrate: CachedKrate<PluginTranslation>,
    dispatchers: KotlinDispatchers
) : EventListener,
    Logger by JUtiltLogger("MoneyAdvancementEvent"),
    CoroutineFeature by CoroutineFeature.Default(dispatchers.IO).withTimings() {
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
                e.player.asKAudience().sendMessage(translation.moneyAdvancement.challengeCompleted(amount))
            }

            AdvancementDisplay.Frame.GOAL -> {
                val amount = mAdvConfig.goal.toDouble()
                launch { economy.addMoney(e.player.uniqueId, amount) }
                e.player.asKAudience().sendMessage(translation.moneyAdvancement.goalCompleted(amount))
            }

            AdvancementDisplay.Frame.TASK -> {
                val amount = mAdvConfig.task.toDouble()
                launch { economy.addMoney(e.player.uniqueId, amount) }
                e.player.asKAudience().sendMessage(translation.moneyAdvancement.taskCompleted(amount))
            }
        }
    }
}
