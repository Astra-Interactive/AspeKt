package ru.astrainteractive.aspekt.module.sethome.di

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.sethome.command.HomeCommandExecutor
import ru.astrainteractive.aspekt.module.sethome.command.di.SetHomeCommandModule
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.astralibs.command.registrar.NeoForgeCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File

class HomesModule(
    commandRegistrarContext: NeoForgeCommandRegistrarContext,
    dataFolder: File,
    stringFormat: StringFormat,
    coreModule: CoreModule,
) {
    private val homeKrateProvider = HomeKrateProvider(
        folder = dataFolder.resolve("homes").also(File::mkdirs),
        stringFormat = stringFormat
    )
    private val homeCommandExecutor = HomeCommandExecutor(
        homeKrateProvider = homeKrateProvider,
        scope = coreModule.ioScope,
        translationKrate = coreModule.translation,
        kyoriKrate = coreModule.kyoriKrate,
        minecraftNativeBridge = coreModule.minecraftNativeBridge
    )

    private val commandModule = SetHomeCommandModule(
        commandRegistrarContext = commandRegistrarContext,
        homeKrateProvider = homeKrateProvider,
        executor = homeCommandExecutor
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            commandModule.lifecycle.onEnable()
        },
        onDisable = {
            homeKrateProvider.clear()
        }
    )
}
