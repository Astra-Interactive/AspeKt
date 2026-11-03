package ru.astrainteractive.aspekt.core.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.core.economy.api.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.core.economy.vault.VaultCurrencyEconomyProviderFactory
import ru.astrainteractive.astralibs.command.api.registrar.PaperCommandRegistrarContext
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent

class BukkitCoreModule(
    val plugin: LifecyclePlugin,
    mainScope: CoroutineScope
) {
    private val eventListener = EventListener.Default()
    private val inventoryClickEventListener = DefaultInventoryClickEvent()
    val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory = VaultCurrencyEconomyProviderFactory()
    val commandRegistrarContext = PaperCommandRegistrarContext(
        mainScope = mainScope,
        plugin = plugin
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            inventoryClickEventListener.onEnable(plugin)
            eventListener.onEnable(plugin)
        },
        onDisable = {
            eventListener.onDisable()
            inventoryClickEventListener.onDisable()
        }
    )
}
