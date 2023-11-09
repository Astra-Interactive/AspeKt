package ru.astrainteractive.aspekt.event.discord.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.event.discord.controllers.RoleController
import ru.astrainteractive.astralibs.async.BukkitDispatchers

interface DiscordEventModule {
    val discordController: RoleController
    val luckPermsController: RoleController
    val scope: CoroutineScope
    val dispatchers: BukkitDispatchers
}
