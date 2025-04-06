package ru.astrainteractive.aspekt.module.adminprivate.data.krate

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminPrivateConfig
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.klibs.kstorage.suspend.flow.StateFlowSuspendMutableKrate
import ru.astrainteractive.klibs.kstorage.suspend.impl.DefaultSuspendMutableKrate
import java.io.File

internal class AdminPrivateKrate(
    file: File,
    stringFormat: StringFormat
) : StateFlowSuspendMutableKrate<AdminPrivateConfig> by DefaultSuspendMutableKrate(
    factory = { AdminPrivateConfig() },
    saver = { value -> stringFormat.writeIntoFile(value, file) },
    loader = {
        if (file.length() == 0L) {
            null
        } else {
            stringFormat.parse<AdminPrivateConfig>(file)
                .onFailure(Throwable::printStackTrace)
                .getOrNull()
        }
    },
)
