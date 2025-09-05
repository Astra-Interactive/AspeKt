package ru.astrainteractive.aspekt.module.claims.data.krate

import kotlinx.coroutines.delay
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.claims.model.ClaimData
import ru.astrainteractive.astralibs.util.parse
import ru.astrainteractive.astralibs.util.writeIntoFile
import ru.astrainteractive.klibs.kstorage.suspend.StateFlowSuspendMutableKrate
import ru.astrainteractive.klibs.kstorage.suspend.impl.DefaultStateFlowSuspendMutableKrate
import java.io.File
import java.util.UUID

class ClaimKrate(
    file: File,
    stringFormat: StringFormat,
    ownerUUID: UUID
) : StateFlowSuspendMutableKrate<ClaimData> by DefaultStateFlowSuspendMutableKrate(
    factory = { ClaimData(ownerUUID) },
    saver = { value ->
        delay(400)
        stringFormat.writeIntoFile(value, file)
    },
    loader = {
        delay(400L)
        if (!file.exists() || file.length() == 0L) {
            null
        } else {
            stringFormat.parse<ClaimData>(file)
                .onFailure(Throwable::printStackTrace)
                .getOrNull()
        }
    },
)
