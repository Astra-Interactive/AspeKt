package ru.astrainteractive.aspekt.event.discord.di

import ru.astrainteractive.aspekt.event.discord.controllers.RoleController

interface DiscordEventModule {
    val discordController: RoleController
    val luckPermsController: RoleController
}
