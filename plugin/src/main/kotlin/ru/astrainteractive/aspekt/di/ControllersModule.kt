package ru.astrainteractive.aspekt.di

import ru.astrainteractive.aspekt.events.discord.controllers.RoleController
import ru.astrainteractive.aspekt.events.sit.SitController
import ru.astrainteractive.aspekt.events.sort.SortController
import ru.astrainteractive.astralibs.Module
import ru.astrainteractive.astralibs.Single

interface ControllersModule : Module {
    val discordController: Single<RoleController>
    val luckPermsController: Single<RoleController>
    val sitController: Single<SitController>
    val sortControllers: Single<SortController>
}
