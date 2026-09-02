package ru.astrainteractive.aspekt.module.claims.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimErrorMapper
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepositoryImpl
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import java.io.File

class ClaimModule(
    stringFormat: StringFormat,
    dataFolder: File,
    ioScope: CoroutineScope,
    translationKrate: CachedKrate<PluginTranslation>
) {
    private val moduleIoScope = CoroutineScope(ioScope.coroutineContext + SupervisorJob())

    val claimsRepository: ClaimsRepository = ClaimsRepositoryImpl(
        folder = dataFolder
            .resolve("claims")
            .also(File::mkdirs),
        stringFormat = stringFormat,
        ioScope = moduleIoScope
    )
    val claimErrorMapper = ClaimErrorMapper(translationKrate = translationKrate)

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = { moduleIoScope.cancel() }
    )

    companion object {
        fun getConfigurationFile(dataFolder: File): File = dataFolder.resolve("claims.yml")
    }
}
