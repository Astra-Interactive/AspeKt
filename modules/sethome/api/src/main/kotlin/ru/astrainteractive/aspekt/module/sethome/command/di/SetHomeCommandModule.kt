package ru.astrainteractive.aspekt.module.sethome.command.di

import ru.astrainteractive.aspekt.module.sethome.command.HomeCommandExecutor
import ru.astrainteractive.aspekt.module.sethome.command.SetHomeCommandRegistrar
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

@Suppress("LongParameterList")
internal class SetHomeCommandModule(
    private val commandRegistrarContext: CommandRegistrarContext,
    private val homeKrateProvider: HomeKrateProvider,
    private val executor: HomeCommandExecutor,
    private val multiplatformCommand: MultiplatformCommand,
    private val translationKrate: CachedKrate<PluginTranslation>,
    private val kyoriKrate: CachedKrate<KyoriComponentSerializer>
) {
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            SetHomeCommandRegistrar(
                homeKrateProvider = homeKrateProvider,
                executor = executor,
                multiplatformCommand = multiplatformCommand,
                registrarContext = commandRegistrarContext,
                translationKrate = translationKrate,
                kyoriKrate = kyoriKrate
            ).register()
        }
    )
}
