package ru.astrainteractive.aspekt.di

import ru.astrainteractive.aspekt.adminprivate.di.AdminPrivateModule
import ru.astrainteractive.aspekt.autobroadcast.di.AutoBroadcastModule
import ru.astrainteractive.aspekt.command.di.CommandManagerModule
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.gui.di.GuiModule
import ru.astrainteractive.klibs.kdi.Module

interface RootModule : Module {
    val coreModule: CoreModule
    val adminPrivateModule: AdminPrivateModule
    val eventsModule: EventsModule
    val guiModule: GuiModule
    val autoBroadcastModule: AutoBroadcastModule
    val commandManagerModule: CommandManagerModule
}
