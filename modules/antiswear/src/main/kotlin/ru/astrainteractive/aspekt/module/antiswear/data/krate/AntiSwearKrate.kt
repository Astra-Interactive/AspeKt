package ru.astrainteractive.aspekt.module.antiswear.data.krate

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.antiswear.data.model.AntiSwearStorage
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.astralibs.util.parse
import ru.astrainteractive.astralibs.util.writeIntoFile
import ru.astrainteractive.klibs.kstorage.suspend.SuspendMutableKrate
import ru.astrainteractive.klibs.kstorage.suspend.impl.DefaultSuspendMutableKrate
import java.io.File

internal class AntiSwearKrate(
    kPlayer: OnlineKPlayer,
    stringFormat: StringFormat,
    folder: File
) : SuspendMutableKrate<AntiSwearStorage> by DefaultSuspendMutableKrate(
    factory = {
        AntiSwearStorage(
            playerName = kPlayer.name,
            uuid = kPlayer.uuid.toString()
        )
    },
    saver = { value ->
        val file = File(folder, "${kPlayer.uuid}.json")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        stringFormat.writeIntoFile(value, file)
    },
    loader = {
        val file = File(folder, "${kPlayer.uuid}.json")
        stringFormat.parse<AntiSwearStorage>(file).getOrNull()
    },
)
