package ru.astrainteractive.aspekt.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactoryImpl
import ru.astrainteractive.astralibs.command.api.registrar.PaperCommandRegistrarContext
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent

class BukkitCoreModule(
    val plugin: LifecyclePlugin,
    val ioScope: CoroutineScope,
    val mainScope: CoroutineScope
) {
    val eventListener = EventListener.Default()
    val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory = CurrencyEconomyProviderFactoryImpl()
    val inventoryClickEventListener = DefaultInventoryClickEvent()
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
            inventoryClickEventListener.onDisable()
            eventListener.onDisable()
        }
    )
}
