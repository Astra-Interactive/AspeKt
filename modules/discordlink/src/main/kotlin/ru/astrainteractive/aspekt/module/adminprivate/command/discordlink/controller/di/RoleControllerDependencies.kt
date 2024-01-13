package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.klibs.kdi.getValue

internal interface RoleControllerDependencies {
    val pluginConfiguration: PluginConfiguration
    val logger: Logger
    val economyProvider: EconomyProvider?
    val tempFileManager: SpigotFileManager
    val translation: PluginTranslation
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(
        coreModule: CoreModule
    ) : RoleControllerDependencies {
        override val pluginConfiguration: PluginConfiguration by coreModule.pluginConfig
        override val logger: Logger by coreModule.logger
        override val economyProvider: EconomyProvider? by coreModule.economyProvider
        override val tempFileManager: SpigotFileManager by coreModule.tempFileManager
        override val translation: PluginTranslation by coreModule.translation
        override val kyoriComponentSerializer by coreModule.kyoriComponentSerializer
    }
}
