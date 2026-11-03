package ru.astrainteractive.aspekt.module.claims.api.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.core.plugin.model.PluginTranslation
import ru.astrainteractive.aspekt.module.claims.api.api.ClaimErrorMapper
import ru.astrainteractive.aspekt.module.claims.api.api.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.api.krate.KrateClaimsRepository
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.io.File

class ClaimModule(
    stringFormat: StringFormat,
    dataFolder: File,
    ioScope: CoroutineScope,
    dispatchers: KotlinDispatchers,
    translationKrate: CachedKrate<PluginTranslation>
) {
    private val moduleIoScope = CoroutineScope(ioScope.coroutineContext + SupervisorJob())

    val claimsRepository: ClaimsRepository = KrateClaimsRepository(
        folder = dataFolder
            .resolve("claims")
            .also(File::mkdirs),
        stringFormat = stringFormat,
        dispatchers = dispatchers,
        ioScope = moduleIoScope
    )
    internal val claimErrorMapper = ClaimErrorMapper(translationKrate = translationKrate)

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = { moduleIoScope.cancel() }
    )

    companion object {
        fun resolveConfigurationFile(dataFolder: File): File = dataFolder.resolve("claims.yml")
    }
}
