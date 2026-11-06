@file:Suppress("ImplementationVisibility")

package ru.astrainteractive.aspekt.module.claims.api.krate

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimData
import ru.astrainteractive.astralibs.util.parse
import ru.astrainteractive.astralibs.util.writeIntoFile
import ru.astrainteractive.klibs.kstorage.suspend.StateFlowSuspendMutableKrate
import ru.astrainteractive.klibs.kstorage.suspend.impl.DefaultStateFlowSuspendMutableKrate
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import java.io.File
import java.util.UUID

private val logger = JUtiltLogger("AspeKt-ClaimKrate")

class ClaimKrate(
    file: File,
    stringFormat: StringFormat,
    ownerUUID: UUID
) : StateFlowSuspendMutableKrate<ClaimData> by DefaultStateFlowSuspendMutableKrate(
    factory = { ClaimData(ownerUUID) },
    saver = { value ->
        stringFormat.writeIntoFile(value, file)
    },
    loader = {
        if (!file.exists() || file.length() == 0L) {
            null
        } else {
            stringFormat.parse<ClaimData>(file)
                .onFailure { throwable ->
                    logger.error(throwable) { "#loader could not parse claim ${file.name} of $ownerUUID" }
                }
                .getOrNull()
        }
    },
)
