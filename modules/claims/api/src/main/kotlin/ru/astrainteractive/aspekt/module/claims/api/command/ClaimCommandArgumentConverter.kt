package ru.astrainteractive.aspekt.module.claims.api.command

import ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentConverter
import ru.astrainteractive.astralibs.command.api.exception.ArgumentConverterException

internal object ClaimCommandArgumentConverter : ArgumentConverter<ClaimCommandArgument> {
    override fun transform(argument: String): ClaimCommandArgument {
        return runCatching {
            ClaimCommandArgument.valueOf(argument)
        }.getOrNull() ?: throw ArgumentConverterException(
            clazz = ClaimCommandArgumentConverter::class.java,
            value = "$argument not found"
        )
    }
}
