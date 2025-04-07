package ru.astrainteractive.aspekt.module.entities.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.entities.command.EntitiesCommandDependencies
import ru.astrainteractive.aspekt.module.entities.command.entities
import ru.astrainteractive.aspekt.module.entities.gui.Router
import ru.astrainteractive.aspekt.module.entities.gui.RouterImpl
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class EntitiesModule(coreModule: CoreModule) {
    private val entitiesCommandDependencies = object : EntitiesCommandDependencies {
        override val plugin: JavaPlugin = coreModule.plugin
        override val router: Router = RouterImpl(
            scope = coreModule.scope,
            dispatchers = coreModule.dispatchers,
            kyoriComponentSerializerKrate = coreModule.kyoriComponentSerializer,
        )
    }

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            entitiesCommandDependencies.entities()
        }
    )
}
