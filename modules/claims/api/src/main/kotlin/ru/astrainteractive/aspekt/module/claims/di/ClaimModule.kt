package ru.astrainteractive.aspekt.module.claims.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.claims.controller.ClaimController
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepositoryImpl
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File

class ClaimModule(
    stringFormat: StringFormat,
    dataFolder: File,
    scope: CoroutineScope
) {
    val claimsRepository: ClaimsRepository = ClaimsRepositoryImpl(
        folder = dataFolder
            .resolve("claims")
            .also(File::mkdirs),
        stringFormat = stringFormat,
        scope = scope
    )
    val claimController = ClaimController(claimsRepository)

    val lifecycle = Lifecycle.Lambda(
        onDisable = {
            claimController.cancel()
        }
    )
}
