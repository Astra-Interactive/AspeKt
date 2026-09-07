package ru.astrainteractive.aspekt.module.sethome.di

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.sethome.command.HomeCommandExecutor
import ru.astrainteractive.aspekt.module.sethome.command.di.SetHomeCommandModule
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.aspekt.module.sethome.model.SetHomeConfiguration
import ru.astrainteractive.aspekt.util.krateOf
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
            file = getConfigurationFile(dataFolder),
            factory = ::SetHomeConfiguration
        )
        .asCachedKrate()

    private val homeKrateProvider = HomeKrateProvider(
        folder = dataFolder.resolve("homes").also(File::mkdirs),
        stringFormat = stringFormat
    )
    private val homeCommandExecutor = HomeCommandExecutor(
        homeKrateProvider = homeKrateProvider,
        scope = coreModule.ioScope,
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translationKrate,
        setHomeConfigKrate = setHomeConfigKrate,
        kyoriKrate = coreModule.kyoriKrate,
    )

    private val setHomeCommandModule = SetHomeCommandModule(
        commandRegistrarContext = commandRegistrarContext,
        homeKrateProvider = homeKrateProvider,
        executor = homeCommandExecutor,
        multiplatformCommand = coreModule.multiplatformCommand,
        translationKrate = coreModule.translationKrate,
        kyoriKrate = coreModule.kyoriKrate
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            setHomeCommandModule.lifecycle.onEnable()
        },
        onReload = {
            setHomeConfigKrate.getValue()
        },
        onDisable = {
            setHomeCommandModule.lifecycle.onDisable()
            homeKrateProvider.clear()
        }
    )

    companion object {
        fun getConfigurationFile(dataFolder: File): File = dataFolder.resolve("sethome.yml")
    }
}
