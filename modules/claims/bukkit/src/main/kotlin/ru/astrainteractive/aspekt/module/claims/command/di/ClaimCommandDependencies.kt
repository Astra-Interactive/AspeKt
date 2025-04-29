package ru.astrainteractive.aspekt.module.claims.command.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimErrorMapper
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface ClaimCommandDependencies {
    val plugin: JavaPlugin
    val scope: CoroutineScope
    val translation: CachedKrate<PluginTranslation>
    val dispatchers: KotlinDispatchers
    val kyoriComponentSerializer: CachedKrate<KyoriComponentSerializer>
    val claimsRepository: ClaimsRepository
    val claimErrorMapper: ClaimErrorMapper

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule,
        override val claimsRepository: ClaimsRepository,
        override val claimErrorMapper: ClaimErrorMapper
    ) : ClaimCommandDependencies {
        override val plugin: JavaPlugin = bukkitCoreModule.plugin
        override val scope: CoroutineScope = coreModule.scope
        override val translation = coreModule.translation
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val kyoriComponentSerializer = coreModule.kyoriComponentSerializer
    }
}
