package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.di

import org.bukkit.configuration.file.FileConfiguration
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.di.DiscordLinkModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import java.io.File

internal interface RoleControllerDependencies {
    val pluginConfiguration: PluginConfiguration
    val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory
    val tempFile: File
    val tempFileConfiguration: FileConfiguration
    val translation: PluginTranslation
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule,
        discordLinkModule: DiscordLinkModule
    ) : RoleControllerDependencies {
        override val pluginConfiguration: PluginConfiguration by coreModule.pluginConfig
        override val tempFile: File = discordLinkModule.tempFile
        override val tempFileConfiguration: FileConfiguration by discordLinkModule.tempFileConfiguration
        override val translation: PluginTranslation by coreModule.translation
        override val kyoriComponentSerializer by coreModule.kyoriComponentSerializer
        override val currencyEconomyProviderFactory = bukkitCoreModule.currencyEconomyProviderFactory
    }
}
