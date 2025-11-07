package ru.astrainteractive.aspekt.module.jail.command.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.jail.command.jail.JailCommandRegistrar
import ru.astrainteractive.aspekt.module.jail.controller.JailController
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApi
import ru.astrainteractive.aspekt.module.jail.data.JailApi
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for Jail module.
 */
internal class JailCommandModule(
    private val coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule,
    private val jailApi: JailApi,
    private val cachedJailApi: CachedJailApi,
    private val jailController: JailController
) {
    private val nodes = buildList {
        JailCommandRegistrar(
            translationKrate = coreModule.translation,
            kyoriKrate = coreModule.kyoriKrate,
            scope = coreModule.ioScope,
            jailApi = jailApi,
            cachedJailApi = cachedJailApi,
            jailController = jailController
        ).createNode().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(bukkitCoreModule.commandRegistrarContext::registerWhenReady)
        }
    )
}
