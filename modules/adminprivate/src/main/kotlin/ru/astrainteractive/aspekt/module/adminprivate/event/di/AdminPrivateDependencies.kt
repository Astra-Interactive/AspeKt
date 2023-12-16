package ru.astrainteractive.aspekt.module.adminprivate.event.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.module.adminprivate.di.AdminPrivateModule
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface AdminPrivateDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val adminPrivateController: AdminPrivateController
    val translation: PluginTranslation
    val translationContext: BukkitTranslationContext

    class Default(
        coreModule: CoreModule,
        adminPrivateModule: AdminPrivateModule
    ) : AdminPrivateDependencies {

        override val adminPrivateController: AdminPrivateController = adminPrivateModule.adminPrivateController

        override val eventListener: EventListener by Provider {
            coreModule.eventListener.value
        }
        override val plugin: JavaPlugin by Provider {
            coreModule.plugin.value
        }
        override val translation: PluginTranslation by coreModule.translation
        override val translationContext: BukkitTranslationContext by Provider {
            coreModule.translationContext
        }
    }
}