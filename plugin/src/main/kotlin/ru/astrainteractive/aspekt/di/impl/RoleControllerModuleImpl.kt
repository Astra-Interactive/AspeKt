package ru.astrainteractive.aspekt.di.impl

import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.discord.controllers.di.RoleControllerModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.klibs.kdi.getValue

class RoleControllerModuleImpl(
    rootModule: RootModule
) : RoleControllerModule {
    override val pluginConfiguration: PluginConfiguration by rootModule.pluginConfig
    override val logger: Logger by rootModule.logger
    override val economyProvider: EconomyProvider? by rootModule.economyProvider
    override val tempFileManager: SpigotFileManager by rootModule.tempFileManager
    override val translation: PluginTranslation by rootModule.translation
}
