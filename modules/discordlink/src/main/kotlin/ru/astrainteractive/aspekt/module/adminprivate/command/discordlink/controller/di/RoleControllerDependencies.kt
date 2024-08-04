package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.di

import org.bukkit.configuration.file.FileConfiguration
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kdi.getValue
import java.io.File

internal interface RoleControllerDependencies {
    val pluginConfiguration: PluginConfiguration
    val economyProvider: EconomyProvider?
    val tempFile: File
    val tempFileConfiguration: FileConfiguration
    val translation: PluginTranslation
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(
        coreModule: CoreModule
    ) : RoleControllerDependencies {
        override val pluginConfiguration: PluginConfiguration by coreModule.pluginConfig
        override val economyProvider: EconomyProvider? by coreModule.economyProvider
        override val tempFile: File = coreModule.tempFile
        override val tempFileConfiguration: FileConfiguration by coreModule.tempFileConfiguration
        override val translation: PluginTranslation by coreModule.translation
        override val kyoriComponentSerializer by coreModule.kyoriComponentSerializer
    }
}
