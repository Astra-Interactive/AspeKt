package ru.astrainteractive.aspekt.module.claims.command.claim

import ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentConverter
import ru.astrainteractive.astralibs.command.api.exception.ArgumentConverterException

object ClaimCommandArgumentConverter : ArgumentConverter<ClaimCommandArgument> {
    override fun transform(argument: String): ClaimCommandArgument {
        return runCatching {
            ClaimCommandArgument.valueOf(argument)
        }.getOrNull() ?: throw ArgumentConverterException(
            clazz = ClaimCommandArgumentConverter::class.java,
            value = "$argument not found"
        )
    }
}
