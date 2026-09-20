package ru.astrainteractive.aspekt.module.rtp.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.rtp.api.SafeLocationProvider
import ru.astrainteractive.aspekt.module.rtp.command.RtpCommandExecutor
import ru.astrainteractive.aspekt.module.rtp.command.RtpCommandRegistrar
import ru.astrainteractive.aspekt.module.rtp.model.RtpConfig
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.asCachedKrate
import java.io.File

class RtpModule(
    coreModule: CoreModule,
    private val commandRegistrarContext: CommandRegistrarContext,
    safeLocationProviderFactory: (CachedKrate<RtpConfig>) -> SafeLocationProvider,
    multiplatformCommand: MultiplatformCommand,
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val rtpConfigKrate = coreModule.yamlFormat
        .krateOf(
            file = getConfigurationFile(coreModule.dataFolder),
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

    private val nodes = RtpCommandRegistrar(
        executor = executor,
        safeLocationProvider = safeLocationProvider,
        multiplatformCommand = multiplatformCommand
    ).createNodes()

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onReload = {
            rtpConfigKrate.getValue()
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )

    companion object {
        fun getConfigurationFile(dataFolder: File): File = dataFolder.resolve("rtp.yml")
    }
}
