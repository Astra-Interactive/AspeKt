package ru.astrainteractive.aspekt.di.impl

import ru.astrainteractive.aspekt.adminprivate.di.AdminPrivateModule
import ru.astrainteractive.aspekt.autobroadcast.di.AutoBroadcastModule
import ru.astrainteractive.aspekt.command.di.CommandManagerModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.gui.di.GuiModule
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
        EventsModule.Default(coreModule, adminPrivateModule)
    }
    override val guiModule: GuiModule by Single {
        GuiModule.Default(coreModule)
    }
    override val autoBroadcastModule by lazy {
        AutoBroadcastModule.Default(coreModule)
    }
    override val commandManagerModule: CommandManagerModule by lazy {
        CommandManagerModule.Default(
            coreModule = coreModule,
            eventsModule = eventsModule,
            adminPrivateModule = adminPrivateModule,
            guiModule = guiModule
        )
    }
}
