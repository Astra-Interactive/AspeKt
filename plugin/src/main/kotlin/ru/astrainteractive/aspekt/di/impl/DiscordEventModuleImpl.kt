package ru.astrainteractive.aspekt.di.impl

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.discord.controllers.DiscordController
import ru.astrainteractive.aspekt.event.discord.controllers.LuckPermsController
import ru.astrainteractive.aspekt.event.discord.controllers.RoleController
import ru.astrainteractive.aspekt.event.discord.di.DiscordEventModule
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class DiscordEventModuleImpl(
    rootModule: RootModule
) : DiscordEventModule {
    private val roleController = RoleControllerModuleImpl(rootModule)
    override val discordController: RoleController by Single {
        DiscordController(roleController, rootModule.translationContext)
    }
    override val luckPermsController: RoleController by Single {
        LuckPermsController(roleController)
    }
    override val scope: CoroutineScope by rootModule.scope
    override val dispatchers: BukkitDispatchers by rootModule.dispatchers
}
