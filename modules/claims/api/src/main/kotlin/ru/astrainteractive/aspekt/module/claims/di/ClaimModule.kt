package ru.astrainteractive.aspekt.module.claims.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimErrorMapper
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepositoryImpl
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.Krate
import java.io.File

class ClaimModule(
    stringFormat: StringFormat,
    dataFolder: File,
    scope: CoroutineScope,
    translationKrate: Krate<PluginTranslation>
) {
    val claimsRepository: ClaimsRepository = ClaimsRepositoryImpl(
        folder = dataFolder
            .resolve("claims")
            .also(File::mkdirs),
        stringFormat = stringFormat,
        scope = scope
    )
    val claimErrorMapper = ClaimErrorMapper(translationKrate = translationKrate)
    val lifecycle = Lifecycle.Lambda(
        onDisable = {}
    )
}
