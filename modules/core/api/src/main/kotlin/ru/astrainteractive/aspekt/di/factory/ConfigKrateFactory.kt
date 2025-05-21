package ru.astrainteractive.aspekt.di.factory

import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.api.value.ValueFactory
import java.io.File

object ConfigKrateFactory : Logger by JUtiltLogger("AstraRating-ConfigKrateFactory") {
    fun <T> fileConfigKrate(
        file: File,
        stringFormat: StringFormat,
        factory: ValueFactory<T>,
        serializer: KSerializer<T>
    ) = DefaultMutableKrate(
        factory = factory,
        loader = {
            val folder = file.parentFile
            if (!folder.exists()) folder.mkdirs()
            stringFormat.parse(serializer, file)
                .onFailure {
                    val defaultFile = when {
                        !file.exists() || file.length() == 0L -> file
                        else -> folder.resolve("${file.nameWithoutExtension}.default.${file.extension}")
                    }
                    if (!defaultFile.exists()) defaultFile.createNewFile()
                    stringFormat.writeIntoFile(serializer, factory.create(), defaultFile)
                }
                .onSuccess { stringFormat.writeIntoFile(serializer, it, file) }
                .getOrElse { factory.create() }
        }
    )

    inline fun <reified T> fileConfigKrate(
        file: File,
        stringFormat: StringFormat,
        factory: ValueFactory<T>,
    ) = fileConfigKrate(
        file = file,
        stringFormat = stringFormat,
        factory = factory,
        serializer = stringFormat.serializersModule.serializer()
    )
}
