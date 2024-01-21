package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.job.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.RoleController
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface DiscordLinkJobDependencies {
    val scope: CoroutineScope
    val dispatchers: KotlinDispatchers
    val configuration: PluginConfiguration
    val luckPermsRoleController: RoleController.Minecraft
    val discordRoleController: RoleController.Discord

    class Default(
        coreModule: CoreModule,
        override val luckPermsRoleController: RoleController.Minecraft,
        override val discordRoleController: RoleController.Discord
    ) : DiscordLinkJobDependencies {
        override val scope: CoroutineScope by coreModule.scope
        override val dispatchers: KotlinDispatchers by coreModule.dispatchers
        override val configuration: PluginConfiguration by coreModule.pluginConfig
    }
}
