package ru.astrainteractive.aspekt.module.claims.command.claim

import ru.astrainteractive.aspekt.module.claims.data.exception.ClaimNotFoundException
import ru.astrainteractive.aspekt.module.claims.data.exception.ClaimNotOwnedException
import ru.astrainteractive.aspekt.module.claims.data.exception.UnderClaimException
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

class ClaimErrorMapper(
    translationKrate: CachedKrate<PluginTranslation>
) : Logger by JUtiltLogger("AspeKt-ClaimErrorMapper") {
    private val translation by translationKrate
    fun toStringDesc(throwable: Throwable): StringDesc.Raw {
        when (throwable) {
            is UnderClaimException -> {
                return translation.claim.chunkUnderClaim
            }

            is ClaimNotFoundException -> {
                return translation.claim.noClaimHere
            }

            is ClaimNotOwnedException -> {
                return translation.claim.notClaimOwner
            }

            else -> {
                error(throwable) { "#toStringDesc" }
                return translation.claim.error
            }
        }
    }
}
