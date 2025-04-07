package ru.astrainteractive.aspekt.module.adminprivate.event.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer

internal interface AdminPrivateDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val adminPrivateController: AdminPrivateController
    val translation: PluginTranslation
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(
        coreModule: CoreModule,
        override val adminPrivateController: AdminPrivateController
    ) : AdminPrivateDependencies {

        override val eventListener: EventListener = coreModule.eventListener
        override val plugin: JavaPlugin = coreModule.plugin
        override val translation: PluginTranslation by coreModule.translation
        override val kyoriComponentSerializer by coreModule.kyoriComponentSerializer
    }
}
