package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.RoleController
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.klibs.kdi.getValue

internal interface DiscordEventDependencies {
    val discordController: RoleController
    val luckPermsController: RoleController
    val addMoneyController: RoleController
    val scope: CoroutineScope
    val dispatchers: BukkitDispatchers

    class Default(
        coreModule: CoreModule,
        override val luckPermsController: RoleController,
        override val discordController: RoleController,
        override val addMoneyController: RoleController
    ) : DiscordEventDependencies {
        override val scope: CoroutineScope by coreModule.scope
        override val dispatchers: BukkitDispatchers by coreModule.dispatchers
    }
}
