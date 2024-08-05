package ru.astrainteractive.aspekt.module.antiswear.command.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepository
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kdi.getValue

internal interface SwearCommandDependencies {
    val plugin: JavaPlugin
    val translation: PluginTranslation
    val kyoriComponentSerializer: KyoriComponentSerializer
    val scope: CoroutineScope
    val swearRepository: SwearRepository

    class Default(
        coreModule: CoreModule,
        override val swearRepository: SwearRepository
    ) : SwearCommandDependencies {
        override val plugin: JavaPlugin by coreModule.plugin
        override val translation: PluginTranslation by coreModule.translation
        override val kyoriComponentSerializer: KyoriComponentSerializer by coreModule.kyoriComponentSerializer
        override val scope: CoroutineScope = coreModule.scope
    }
}
