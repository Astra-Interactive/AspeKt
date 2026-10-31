package ru.astrainteractive.aspekt.module.sethome.api.di

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.core.di.CoreModule
import ru.astrainteractive.aspekt.core.util.krateOf
import ru.astrainteractive.aspekt.module.sethome.api.command.HomeCommandExecutor
import ru.astrainteractive.aspekt.module.sethome.api.command.di.SetHomeCommandModule
import ru.astrainteractive.aspekt.module.sethome.api.krate.KratePlayerHomesRepository
import ru.astrainteractive.aspekt.module.sethome.api.model.SetHomeConfiguration
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.asCachedKrate
import java.io.File

class SetHomeModule(
    commandRegistrarContext: CommandRegistrarContext,
    dataFolder: File,
    stringFormat: StringFormat,
    coreModule: CoreModule,
) {
    private val setHomeConfigKrate = coreModule.yamlFormat
        .krateOf(
            file = resolveConfigurationFile(dataFolder),
            factory = ::SetHomeConfiguration
        )
        .asCachedKrate()

    private val playerHomesRepository = KratePlayerHomesRepository(
        folder = dataFolder.resolve("homes").also(File::mkdirs),
        stringFormat = stringFormat
    )
    private val homeCommandExecutor = HomeCommandExecutor(
        playerHomesRepository = playerHomesRepository,
        scope = coreModule.ioScope,
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translationKrate,
        setHomeConfigKrate = setHomeConfigKrate,
    )

    private val setHomeCommandModule = SetHomeCommandModule(
        commandRegistrarContext = commandRegistrarContext,
        playerHomesRepository = playerHomesRepository,
        executor = homeCommandExecutor,
        multiplatformCommand = coreModule.multiplatformCommand,
        commandExceptionHandler = coreModule.commandExceptionHandler,
        unconfinedScope = coreModule.unconfinedScope
    )

    @Suppress("TrivialLambdaReference")
    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            setHomeCommandModule.lifecycle.onEnable()
        },
        onReload = {
            setHomeConfigKrate.getValue()
        },
        onDisable = {
            setHomeCommandModule.lifecycle.onDisable()
            playerHomesRepository.clear()
        }
    )

    companion object {
        fun resolveConfigurationFile(dataFolder: File): File = dataFolder.resolve("sethome.yml")
    }
}
