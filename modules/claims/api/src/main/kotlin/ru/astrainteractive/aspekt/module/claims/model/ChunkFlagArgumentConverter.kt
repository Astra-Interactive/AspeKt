package ru.astrainteractive.aspekt.module.claims.model

import ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentConverter
import ru.astrainteractive.astralibs.command.api.exception.ArgumentConverterException

object ChunkFlagArgumentConverter : ArgumentConverter<ChunkFlag> {
    override fun transform(argument: String): ChunkFlag {
        return runCatching {
            ChunkFlag.valueOf(argument)
        }.getOrNull() ?: throw ArgumentConverterException(
            clazz = ChunkFlagArgumentConverter::class.java,
            value = "$argument not found"
        )
    }
}
