package ru.astrainteractive.aspekt.event.adminprivate.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.adminprivate.di.AdminPrivateModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface AdminPrivateDependencies {
    val eventListener: EventListener
    val plugin: AspeKt
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
        override val plugin: AspeKt by Provider {
            coreModule.plugin.value
        }
        override val translation: PluginTranslation by coreModule.translation
        override val translationContext: BukkitTranslationContext by Provider {
            coreModule.translationContext
        }
    }
}
