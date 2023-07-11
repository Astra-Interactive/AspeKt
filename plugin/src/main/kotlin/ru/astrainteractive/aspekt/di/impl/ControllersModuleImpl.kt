package ru.astrainteractive.aspekt.di.impl

import ru.astrainteractive.aspekt.di.ControllersModule
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.events.di.EventsModule
import ru.astrainteractive.aspekt.events.discord.controllers.DiscordController
import ru.astrainteractive.aspekt.events.discord.controllers.LuckPermsController
import ru.astrainteractive.aspekt.events.discord.controllers.RoleController
import ru.astrainteractive.aspekt.events.sit.SitController
import ru.astrainteractive.aspekt.events.sort.SortController
import ru.astrainteractive.astralibs.Single
import ru.astrainteractive.astralibs.getValue

object ControllersModuleImpl : ControllersModule {
    private val rootModule: RootModule by RootModuleImpl
    private val eventsModule: EventsModule by EventsModuleImpl

    override val discordController: Single<RoleController> = Single {
        DiscordController(
            pluginConfiguration = rootModule.pluginConfig,
            logger = rootModule.logger
        )
    }
    override val luckPermsController: Single<RoleController> = Single {
        LuckPermsController(
            pluginConfiguration = rootModule.pluginConfig,
            logger = rootModule.logger
        )
    }
    override val sitController: Single<SitController> = Single {
        SitController(eventsModule)
    }
    override val sortControllers: Single<SortController> = Single {
        SortController()
    }
}
