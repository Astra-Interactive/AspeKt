package ru.astrainteractive.aspekt.module.rtp.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.rtp.api.SafeLocationProvider
import ru.astrainteractive.aspekt.module.rtp.command.RtpCommandExecutor
import ru.astrainteractive.aspekt.module.rtp.command.RtpCommandRegistrar
import ru.astrainteractive.aspekt.module.rtp.model.RtpConfig
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.asCachedKrate

class RtpModule(
    coreModule: CoreModule,
    private val commandRegistrarContext: CommandRegistrarContext,
    private val safeLocationProviderFactory: (CachedKrate<RtpConfig>) -> SafeLocationProvider,
    private val multiplatformCommand: MultiplatformCommand,
) {
    private val rtpConfigKrate = coreModule.yamlFormat
        .krateOf(
            file = coreModule.dataFolder.resolve("rtp.yml"),
            factory = ::RtpConfig
        )
        .asCachedKrate()
    private val safeLocationProvider = safeLocationProviderFactory.invoke(rtpConfigKrate)
    private val executor = RtpCommandExecutor(
        ioScope = coreModule.ioScope,
        safeLocationProvider = safeLocationProvider,
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translationKrate,
        kyoriKrate = coreModule.kyoriKrate,
        rtpConfigKrate = rtpConfigKrate,
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            RtpCommandRegistrar(
                executor = executor,
                safeLocationProvider = safeLocationProvider,
                multiplatformCommand = multiplatformCommand,
                registrarContext = commandRegistrarContext
            ).register()
        },
        onReload = {
            rtpConfigKrate.getValue()
        }
    )
}
