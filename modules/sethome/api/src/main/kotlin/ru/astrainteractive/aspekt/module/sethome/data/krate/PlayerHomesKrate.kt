package ru.astrainteractive.aspekt.module.sethome.data.krate

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.sethome.model.PlayerHome
import ru.astrainteractive.astralibs.util.parse
import ru.astrainteractive.astralibs.util.writeIntoFile
import ru.astrainteractive.klibs.kstorage.suspend.StateFlowSuspendMutableKrate
import ru.astrainteractive.klibs.kstorage.suspend.impl.DefaultStateFlowSuspendMutableKrate
import java.io.File
import java.util.UUID

class PlayerHomesKrate(
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
                .onFailure(Throwable::printStackTrace)
                .getOrNull()
        }
    },
)
