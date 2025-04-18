package ru.astrainteractive.aspekt.module.claims.command.claim

import ru.astrainteractive.aspekt.module.claims.data.exception.ClaimNotFoundException
import ru.astrainteractive.aspekt.module.claims.data.exception.ClaimNotOwnedException
import ru.astrainteractive.aspekt.module.claims.data.exception.UnderClaimException
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.Krate

class ClaimErrorMapper(
    translationKrate: Krate<PluginTranslation>
) : Logger by JUtiltLogger("AspeKt-ClaimErrorMapper") {
    private val translation by translationKrate
    fun toStringDesc(throwable: Throwable): StringDesc.Raw {
        when (throwable) {
            is UnderClaimException -> {
                return StringDesc.Raw("Кто-то ухе заприватил тут")
            }

            is ClaimNotFoundException -> {
                return StringDesc.Raw("Тут нет привата")
            }

            is ClaimNotOwnedException -> {
                return StringDesc.Raw("Вы не владелец этого привата")
            }

            else -> {
                error(throwable) { "#toStringDesc" }
                return StringDesc.Raw("Произошла неизвестная ошибка. Обратитесь к администратору")
            }
        }
    }
}
