package ru.astrainteractive.aspekt.module.sethome.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.module.sethome.command.HomeCommandExecutor
import ru.astrainteractive.aspekt.module.sethome.command.SetHomeCommandRegistrar
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

@Suppress("LongParameterList")
internal class SetHomeCommandModule(
    homeKrateProvider: HomeKrateProvider,
    executor: HomeCommandExecutor,
    multiplatformCommand: MultiplatformCommand,
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    unconfinedScope: CoroutineScope,
    private val commandRegistrarContext: CommandRegistrarContext
) {
    private val moduleUnconfinedScope = CoroutineScope(unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = SetHomeCommandRegistrar(
        homeKrateProvider = homeKrateProvider,
        executor = executor,
        multiplatformCommand = multiplatformCommand,
        translationKrate = translationKrate,
        kyoriKrate = kyoriKrate
    ).createNodes()

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
