package ru.astrainteractive.aspekt.module.antiswear.command.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepository
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer

internal interface SwearCommandDependencies {
    val plugin: JavaPlugin
    val translation: PluginTranslation
    val kyoriComponentSerializer: KyoriComponentSerializer
    val scope: CoroutineScope
    val swearRepository: SwearRepository

    class Default(
        private val coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule,
        override val swearRepository: SwearRepository
    ) : SwearCommandDependencies {
        override val plugin: JavaPlugin = bukkitCoreModule.plugin
        override val translation: PluginTranslation
            get() = coreModule.translation.cachedValue
        override val kyoriComponentSerializer: KyoriComponentSerializer
            get() = coreModule.kyoriComponentSerializer.cachedValue
        override val scope: CoroutineScope = coreModule.scope
    }
}
