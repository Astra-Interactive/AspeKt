package ru.astrainteractive.aspekt.event.discord.controllers.di

import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.logging.Logger

interface RoleControllerModule {
    val pluginConfiguration: PluginConfiguration
    val logger: Logger
    val economyProvider: EconomyProvider?
    val tempFileManager: SpigotFileManager
    val translation: PluginTranslation
}
