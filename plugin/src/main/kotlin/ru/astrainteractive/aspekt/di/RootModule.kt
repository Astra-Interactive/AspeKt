package ru.astrainteractive.aspekt.di

import ru.astrainteractive.aspekt.command.di.CommandManagerModule
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.gui.di.GuiModule
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.di.DiscordLinkModule
import ru.astrainteractive.aspekt.module.adminprivate.di.AdminPrivateModule
import ru.astrainteractive.aspekt.module.autobroadcast.di.AutoBroadcastModule
import ru.astrainteractive.aspekt.module.menu.di.MenuModule
import ru.astrainteractive.klibs.kdi.Module

interface RootModule : Module {
    val coreModule: CoreModule
    val adminPrivateModule: AdminPrivateModule
    val eventsModule: EventsModule
    val menuModule: MenuModule
    val guiModule: GuiModule
    val autoBroadcastModule: AutoBroadcastModule
    val discordLinkModule: DiscordLinkModule
    val commandManagerModule: CommandManagerModule
}
