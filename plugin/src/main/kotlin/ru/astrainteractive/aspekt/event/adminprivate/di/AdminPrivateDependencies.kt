package ru.astrainteractive.aspekt.event.adminprivate.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.di.RootModule
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
        rootModule: RootModule,
        override val adminPrivateController: AdminPrivateController
    ) : AdminPrivateDependencies {
        override val eventListener: EventListener by Provider {
            rootModule.eventListener.value
        }
        override val plugin: AspeKt by Provider {
            rootModule.plugin.value
        }
        override val translation: PluginTranslation by rootModule.translation
        override val translationContext: BukkitTranslationContext by Provider {
            rootModule.translationContext
        }
    }
}
