package ru.astrainteractive.aspekt.module.claims.data.krate

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.claims.model.ClaimData
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.klibs.kstorage.suspend.flow.StateFlowSuspendMutableKrate
import ru.astrainteractive.klibs.kstorage.suspend.impl.DefaultSuspendMutableKrate
import java.io.File

class ClaimKrate(
    file: File,
    stringFormat: StringFormat
) : StateFlowSuspendMutableKrate<ClaimData> by DefaultSuspendMutableKrate(
    factory = { ClaimData() },
    saver = { value -> stringFormat.writeIntoFile(value, file) },
    loader = {
        if (!file.exists() || file.length() == 0L) {
            null
        } else {
            stringFormat.parse<ClaimData>(file)
                .onFailure(Throwable::printStackTrace)
                .getOrNull()
        }
    },
)
