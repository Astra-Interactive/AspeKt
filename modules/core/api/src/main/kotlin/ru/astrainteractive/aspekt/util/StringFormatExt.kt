package ru.astrainteractive.aspekt.util

import kotlinx.serialization.StringFormat
import ru.astrainteractive.astralibs.util.parseOrWriteIntoDefault
import ru.astrainteractive.astralibs.util.writeIntoFile
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.api.value.ValueFactory
import java.io.File

inline fun <reified T> StringFormat.krateOf(
    file: File,
    factory: ValueFactory<T>
): DefaultMutableKrate<T> = DefaultMutableKrate(
    factory = factory,
    loader = {
        parseOrWriteIntoDefault<T?>(
            file = file,
            default = factory::create
        )
    },
    saver = { value ->
        if (value == null) {
            file.delete()
        } else {
            writeIntoFile<T>(
                value = value,
                file = file
            )
        }
    }
)
