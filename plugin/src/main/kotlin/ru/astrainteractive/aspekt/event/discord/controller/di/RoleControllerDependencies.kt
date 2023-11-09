package ru.astrainteractive.aspekt.event.discord.controller.di

import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.klibs.kdi.getValue

interface RoleControllerDependencies {
    val pluginConfiguration: PluginConfiguration
    val logger: Logger
    val economyProvider: EconomyProvider?
    val tempFileManager: SpigotFileManager
    val translation: PluginTranslation
    val translationContext: BukkitTranslationContext

    class Default(
        rootModule: RootModule
    ) : RoleControllerDependencies {
        override val pluginConfiguration: PluginConfiguration by rootModule.pluginConfig
        override val logger: Logger by rootModule.logger
        override val economyProvider: EconomyProvider? by rootModule.economyProvider
        override val tempFileManager: SpigotFileManager by rootModule.tempFileManager
        override val translation: PluginTranslation by rootModule.translation
        override val translationContext: BukkitTranslationContext = rootModule.translationContext
    }

}
