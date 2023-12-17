package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.DiscordEvent
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.DiscordController
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.LuckPermsController
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.RoleController
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.di.RoleControllerDependencies

interface DiscordLinkModule {
    val discordEvent: DiscordEvent

    val discordController: RoleController
    val luckPermsController: RoleController

    class Default(coreModule: CoreModule) : DiscordLinkModule {
        private val roleControllerDependencies by lazy {
            RoleControllerDependencies.Default(coreModule)
        }

        override val luckPermsController: RoleController by lazy {
            LuckPermsController(roleControllerDependencies)
        }

        override val discordController: RoleController by lazy {
            DiscordController(roleControllerDependencies)
        }

        private val dependencies by lazy {
            DiscordEventDependencies.Default(
                coreModule = coreModule,
                discordController = discordController,
                luckPermsController = luckPermsController
            )
        }

        override val discordEvent: DiscordEvent by lazy {
            DiscordEvent(dependencies)
        }
    }
}
