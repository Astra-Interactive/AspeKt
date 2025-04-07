package ru.astrainteractive.aspekt.module.adminprivate.command.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer

internal interface AdminPrivateCommandDependencies {
    val plugin: JavaPlugin
    val adminPrivateController: AdminPrivateController
    val scope: CoroutineScope
    val translation: PluginTranslation
    val dispatchers: BukkitDispatchers
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(
        coreModule: CoreModule,
        override val adminPrivateController: AdminPrivateController
    ) : AdminPrivateCommandDependencies {
        override val plugin: JavaPlugin = coreModule.plugin
        override val scope: CoroutineScope = coreModule.scope
        override val translation: PluginTranslation by coreModule.translation
        override val dispatchers: BukkitDispatchers = coreModule.dispatchers
        override val kyoriComponentSerializer: KyoriComponentSerializer by coreModule.kyoriComponentSerializer
    }
}
