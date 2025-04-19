package ru.astrainteractive.aspekt.module.claims.event.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.util.getValue

internal interface ClaimDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val translation: PluginTranslation
    val kyoriComponentSerializer: KyoriComponentSerializer
    val claimsRepository: ClaimsRepository

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule,
        override val claimsRepository: ClaimsRepository
    ) : ClaimDependencies {

        override val eventListener: EventListener = bukkitCoreModule.eventListener
        override val plugin: JavaPlugin = bukkitCoreModule.plugin
        override val translation: PluginTranslation by coreModule.translation
        override val kyoriComponentSerializer by coreModule.kyoriComponentSerializer
    }
}
