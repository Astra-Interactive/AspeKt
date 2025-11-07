package ru.astrainteractive.aspekt.module.antiswear.command.swearfilter

import ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentConverter
import ru.astrainteractive.astralibs.command.api.exception.ArgumentConverterException

internal object OnOffArgumentConverter : ArgumentConverter<Boolean> {
    override fun transform(argument: String): Boolean {
        return when (argument) {
            "on" -> true
            "off" -> false
            else -> throw ArgumentConverterException(OnOffArgumentConverter::class.java, "Wrong argument $argument")
        }
    }
}
