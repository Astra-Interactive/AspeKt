package ru.astrainteractive.aspekt.module.sethome.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.StringFormat
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.sethome.command.HomeCommandExecutor
import ru.astrainteractive.aspekt.module.sethome.command.homes
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.aspekt.module.sethome.teleport.ForgeTeleportApi
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File

class HomesModule(
    registerCommandsEventFlow: Flow<RegisterCommandsEvent>,
    dataFolder: File,
    stringFormat: StringFormat,
    coreModule: CoreModule,
) {
    val homeKrateProvider = HomeKrateProvider(
        folder = dataFolder.resolve("homes").also(File::mkdirs),
        stringFormat = stringFormat
    )
    val teleporter = ForgeTeleportApi()
    val homeCommandExecutor = HomeCommandExecutor(
        homeKrateProvider = homeKrateProvider,
        scope = coreModule.scope,
        teleportApi = teleporter,
        translationKrate = coreModule.translation,
        minecraftMessenger = coreModule.minecraftMessenger
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            coreModule.scope.launch(Dispatchers.IO) {
                registerCommandsEventFlow
                    .first()
                    .homes(
                        homeKrateProvider = homeKrateProvider,
                        homeCommandExecutor = homeCommandExecutor
                    )
            }
        },
        onDisable = {
            teleporter.clear()
            homeKrateProvider.clear()
        }
    )
}
