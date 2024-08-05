package ru.astrainteractive.aspekt.module.antiswear.data.krate

import kotlinx.serialization.StringFormat
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.antiswear.data.model.AntiSwearStorage
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.klibs.kstorage.suspend.SuspendMutableKrate
import ru.astrainteractive.klibs.kstorage.suspend.impl.DefaultSuspendMutableKrate
import java.io.File

internal class AntiSwearKrate(
    player: Player,
    stringFormat: StringFormat,
    folder: File
) : SuspendMutableKrate<AntiSwearStorage> by DefaultSuspendMutableKrate(
    factory = {
        AntiSwearStorage(
            playerName = player.name,
            uuid = player.uniqueId.toString()
        )
    },
    saver = { value ->
        val file = File(folder, "${player.uniqueId}.json")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        stringFormat.writeIntoFile(value, file)
    },
    loader = {
        val file = File(folder, "${player.uniqueId}.json")
        stringFormat.parse<AntiSwearStorage>(file).getOrNull()
    },
)
