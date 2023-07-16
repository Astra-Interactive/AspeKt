package ru.astrainteractive.aspekt.event.discord.controllers.di

import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.logging.Logger

interface RoleControllerModule {
    val pluginConfiguration: PluginConfiguration
    val logger: Logger
}
