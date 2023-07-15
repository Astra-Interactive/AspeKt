package ru.astrainteractive.aspekt.di.impl

import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.discord.controllers.DiscordController
import ru.astrainteractive.aspekt.event.discord.controllers.LuckPermsController
import ru.astrainteractive.aspekt.event.discord.controllers.RoleController
import ru.astrainteractive.aspekt.event.discord.di.DiscordEventModule
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class DiscordEventModuleImpl(
    rootModule: RootModule
) : DiscordEventModule {
    private val roleController = RoleControllerModuleImpl(rootModule)
    override val discordController: RoleController by Single {
        DiscordController(roleController)
    }
    override val luckPermsController: RoleController by Single {
        LuckPermsController(roleController)
    }
}
