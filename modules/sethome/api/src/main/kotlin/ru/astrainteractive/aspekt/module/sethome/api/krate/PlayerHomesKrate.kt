package ru.astrainteractive.aspekt.module.sethome.api.krate

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.sethome.api.model.PlayerHome
import ru.astrainteractive.astralibs.util.parse
import ru.astrainteractive.astralibs.util.writeIntoFile
import ru.astrainteractive.klibs.kstorage.suspend.StateFlowSuspendMutableKrate
import ru.astrainteractive.klibs.kstorage.suspend.impl.DefaultStateFlowSuspendMutableKrate
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import java.io.File
import java.util.UUID

private val logger = JUtiltLogger("AspeKt-PlayerHomesKrate")

internal class PlayerHomesKrate(
    folder: File,
    stringFormat: StringFormat,
    uuid: UUID
) : StateFlowSuspendMutableKrate<List<PlayerHome>> by DefaultStateFlowSuspendMutableKrate(
    factory = { emptyList() },
    saver = { value ->
        val file = folder.resolve("$uuid.json").also(File::createNewFile)
        stringFormat.writeIntoFile(value, file)
    },
    loader = {
        val file = folder.resolve("$uuid.json")
        if (!file.exists() || file.length() == 0L) {
            null
        } else {
            stringFormat.parse<List<PlayerHome>>(file)
                .onFailure { throwable ->
                    logger.error(throwable) { "#loader could not parse homes ${file.name} of $uuid" }
                }
                .getOrNull()
        }
    },
)
