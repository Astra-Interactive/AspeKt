package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.filemanager.FileConfigurationManager
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.klibs.kdi.getValue

internal interface RoleControllerDependencies {
    val pluginConfiguration: PluginConfiguration
    val economyProvider: EconomyProvider?
    val tempFileManager: FileConfigurationManager
    val translation: PluginTranslation
    val kyoriComponentSerializer: KyoriComponentSerializer
    class Default(
        coreModule: CoreModule
    ) : RoleControllerDependencies {
        override val pluginConfiguration: PluginConfiguration by coreModule.pluginConfig
        override val economyProvider: EconomyProvider? by coreModule.economyProvider
        override val tempFileManager: FileConfigurationManager by coreModule.tempFileManager
        override val translation: PluginTranslation by coreModule.translation
        override val kyoriComponentSerializer by coreModule.kyoriComponentSerializer
    }
}
