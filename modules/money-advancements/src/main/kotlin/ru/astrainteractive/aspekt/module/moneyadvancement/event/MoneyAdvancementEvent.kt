package ru.astrainteractive.aspekt.module.moneyadvancement.event

import io.papermc.paper.advancement.AdvancementDisplay
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class MoneyAdvancementEvent(
    configurationProvider: Provider<PluginConfiguration>,
    economyProvider: Provider<EconomyProvider?>,
    kyoriComponentSerializerProvider: Provider<KyoriComponentSerializer>,
    translationProvider: Provider<PluginTranslation>
) : EventListener, Logger by JUtiltLogger("MoneyAdvancementEvent"), AsyncComponent() {
    private val configuration by configurationProvider
    private val economy by economyProvider
    private val kyoriComponentSerializer by kyoriComponentSerializerProvider
    private val translation by translationProvider

    @EventHandler
    fun onAdvancement(e: PlayerAdvancementDoneEvent) {
        val economy = economy ?: run {
            error { "#onAdvancement economy not found" }
            return
        }
        val frame = e.advancement.display?.frame() ?: return
        info { "#onAdvancement ${e.advancement.key} ${e.advancement.display?.title()}" }
        when (frame) {
            AdvancementDisplay.Frame.CHALLENGE -> with(kyoriComponentSerializer) {
                val amount = configuration.advancementMoney.challenge.toDouble()
                launch { economy.addMoney(e.player.uniqueId, amount) }
                e.player.sendMessage(translation.moneyAdvancement.challengeCompleted(amount).component)
            }

            AdvancementDisplay.Frame.GOAL -> with(kyoriComponentSerializer) {
                val amount = configuration.advancementMoney.goal.toDouble()
                launch { economy.addMoney(e.player.uniqueId, amount) }
                e.player.sendMessage(translation.moneyAdvancement.goalCompleted(amount).component)
            }

            AdvancementDisplay.Frame.TASK -> with(kyoriComponentSerializer) {
                val amount = configuration.advancementMoney.task.toDouble()
                launch { economy.addMoney(e.player.uniqueId, amount) }
                e.player.sendMessage(translation.moneyAdvancement.taskCompleted(amount).component)
            }
        }
    }
}
