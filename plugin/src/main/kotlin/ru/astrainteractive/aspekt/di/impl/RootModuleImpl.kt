package ru.astrainteractive.aspekt.di.impl

import ru.astrainteractive.aspekt.command.di.CommandManagerModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.gui.di.GuiModule
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.di.DiscordLinkModule
import ru.astrainteractive.aspekt.module.adminprivate.di.AdminPrivateModule
import ru.astrainteractive.aspekt.module.autobroadcast.di.AutoBroadcastModule
import ru.astrainteractive.aspekt.module.menu.di.MenuModule
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class RootModuleImpl : RootModule {

    override val coreModule: CoreModule by lazy {
        CoreModule.Default()
    }
    override val adminPrivateModule: AdminPrivateModule by lazy {
        AdminPrivateModule.Default(coreModule)
    }
    override val eventsModule: EventsModule by Single {
        EventsModule.Default(coreModule)
    }
    override val menuModule: MenuModule by lazy {
        MenuModule.Default(coreModule)
    }
    override val guiModule: GuiModule by Single {
        GuiModule.Default(coreModule)
    }
    override val autoBroadcastModule by lazy {
        AutoBroadcastModule.Default(coreModule)
    }
    override val discordLinkModule: DiscordLinkModule by lazy {
        DiscordLinkModule.Default(coreModule)
    }

    override val commandManagerModule: CommandManagerModule by lazy {
        CommandManagerModule.Default(
            coreModule = coreModule,
            eventsModule = eventsModule,
            guiModule = guiModule,
            menuModule = menuModule
        )
    }
}
