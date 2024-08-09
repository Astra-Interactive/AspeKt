package ru.astrainteractive.aspekt.module.moneyadvancement.event

import io.papermc.paper.advancement.AdvancementDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class MoneyAdvancementEvent(
    configurationProvider: Provider<PluginConfiguration>,
    economyProvider: Provider<EconomyProvider?>,
    kyoriComponentSerializerProvider: Provider<KyoriComponentSerializer>,
    translationProvider: Provider<PluginTranslation>
) : EventListener {
    private val configuration by configurationProvider
    private val economy by economyProvider
    private val kyoriComponentSerializer by kyoriComponentSerializerProvider
    private val translation by translationProvider

    @EventHandler
    fun onAdvancement(e: PlayerAdvancementDoneEvent) {
        val economy = economy ?: return
        val frame = e.advancement.display?.frame() ?: AdvancementDisplay.Frame.TASK
        when (frame) {
            AdvancementDisplay.Frame.CHALLENGE -> with(kyoriComponentSerializer) {
                val amount = configuration.advancementMoney.challenge.toDouble()
                economy.addMoney(e.player.uniqueId, amount)
                e.player.sendMessage(translation.moneyAdvancement.challengeCompleted(amount).component)
            }

            AdvancementDisplay.Frame.GOAL -> with(kyoriComponentSerializer) {
                val amount = configuration.advancementMoney.goal.toDouble()
                economy.addMoney(e.player.uniqueId, amount)
                e.player.sendMessage(translation.moneyAdvancement.goalCompleted(amount).component)
            }

            AdvancementDisplay.Frame.TASK -> with(kyoriComponentSerializer) {
                val amount = configuration.advancementMoney.task.toDouble()
                economy.addMoney(e.player.uniqueId, amount)
                e.player.sendMessage(translation.moneyAdvancement.taskCompleted(amount).component)
            }
        }
    }
}
