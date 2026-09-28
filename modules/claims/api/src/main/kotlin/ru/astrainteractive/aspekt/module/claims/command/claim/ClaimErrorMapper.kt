package ru.astrainteractive.aspekt.module.claims.command.claim

import ru.astrainteractive.aspekt.module.claims.data.exception.ClaimNotFoundException
import ru.astrainteractive.aspekt.module.claims.data.exception.ClaimNotOwnedException
import ru.astrainteractive.aspekt.module.claims.data.exception.UnderClaimException
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

class ClaimErrorMapper(
    translationKrate: CachedKrate<PluginTranslation>
) : Logger by JUtiltLogger("AspeKt-ClaimErrorMapper") {
    private val translation by translationKrate
    fun toMessage(throwable: Throwable): LocalizedText {
        when (throwable) {
            is UnderClaimException -> {
                return translation.claim.alreadyClaimed
            }

            is ClaimNotFoundException -> {
                return translation.claim.noneHere
            }

            is ClaimNotOwnedException -> {
                return translation.claim.notOwner
            }

            else -> {
                error(throwable) { "#toMessage" }
                return translation.claim.error
            }
        }
    }
}
