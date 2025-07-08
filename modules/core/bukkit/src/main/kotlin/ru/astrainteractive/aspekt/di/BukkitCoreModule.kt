package ru.astrainteractive.aspekt.di

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactoryImpl
import ru.astrainteractive.aspekt.util.lifecycleEventFlow
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent

class BukkitCoreModule(
    val plugin: LifecyclePlugin,
    val scope: CoroutineScope,
    val mainScope: CoroutineScope
) {
    val eventListener = EventListener.Default()
    val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory = CurrencyEconomyProviderFactoryImpl()
    val inventoryClickEventListener = DefaultInventoryClickEvent()
    val commandsRegistrarFlow = plugin.lifecycleEventFlow(LifecycleEvents.COMMANDS)
        .shareIn(mainScope, SharingStarted.Eagerly, 1)

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
