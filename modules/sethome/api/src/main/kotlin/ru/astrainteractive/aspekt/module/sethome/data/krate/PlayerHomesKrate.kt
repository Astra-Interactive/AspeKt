package ru.astrainteractive.aspekt.module.sethome.data.krate

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.sethome.model.PlayerHome
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.klibs.kstorage.suspend.flow.StateFlowSuspendMutableKrate
import ru.astrainteractive.klibs.kstorage.suspend.impl.DefaultSuspendMutableKrate
import java.io.File
import java.util.UUID

class PlayerHomesKrate(
    folder: File,
    stringFormat: StringFormat,
    uuid: UUID
) : StateFlowSuspendMutableKrate<List<PlayerHome>> by DefaultSuspendMutableKrate(
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
