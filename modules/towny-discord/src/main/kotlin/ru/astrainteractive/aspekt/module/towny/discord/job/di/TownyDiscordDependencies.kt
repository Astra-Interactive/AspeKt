package ru.astrainteractive.aspekt.module.towny.discord.job.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.RoleController
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.di.DiscordLinkModule
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface TownyDiscordDependencies {
    val scope: CoroutineScope
    val dispatchers: KotlinDispatchers
    val configuration: PluginConfiguration
    val discordRoleController: RoleController.Discord

    class Default(
        coreModule: CoreModule,
        discordLinkModule: DiscordLinkModule
    ) : TownyDiscordDependencies {
        override val scope: CoroutineScope = coreModule.scope
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val configuration: PluginConfiguration by coreModule.pluginConfig
        override val discordRoleController: RoleController.Discord = discordLinkModule.discordController
    }
}
