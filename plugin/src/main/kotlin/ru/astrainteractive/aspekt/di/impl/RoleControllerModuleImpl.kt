package ru.astrainteractive.aspekt.di.impl

import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.discord.controllers.di.RoleControllerModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.klibs.kdi.getValue

class RoleControllerModuleImpl(
    rootModule: RootModule
) : RoleControllerModule {
    override val pluginConfiguration: PluginConfiguration by rootModule.pluginConfig
    override val logger: Logger by rootModule.logger
}
