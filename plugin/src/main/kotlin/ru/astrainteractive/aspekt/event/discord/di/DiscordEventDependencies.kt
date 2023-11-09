package ru.astrainteractive.aspekt.event.discord.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.discord.controller.DiscordController
import ru.astrainteractive.aspekt.event.discord.controller.LuckPermsController
import ru.astrainteractive.aspekt.event.discord.controller.RoleController
import ru.astrainteractive.aspekt.event.discord.controller.di.RoleControllerDependencies
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface DiscordEventDependencies {
    val discordController: RoleController
    val luckPermsController: RoleController
    val scope: CoroutineScope
    val dispatchers: BukkitDispatchers

    class Default(rootModule: RootModule) : DiscordEventDependencies {
        private val roleControlelrDependencies by Provider {
            RoleControllerDependencies.Default(rootModule)
        }
        override val discordController: RoleController by Single {
            DiscordController(roleControlelrDependencies)
        }
        override val luckPermsController: RoleController by Single {
            LuckPermsController(roleControlelrDependencies)
        }
        override val scope: CoroutineScope by rootModule.scope
        override val dispatchers: BukkitDispatchers by rootModule.dispatchers

    }
}
