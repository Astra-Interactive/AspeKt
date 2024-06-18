package ru.astrainteractive.aspekt.module.antiswear.data

import kotlinx.serialization.StringFormat
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.antiswear.model.AntiSwearStorage
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.klibs.kstorage.api.MutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import java.io.File

internal class AntiSwearKrate(
    player: Player,
    stringFormat: StringFormat,
    folder: File
) : MutableKrate<AntiSwearStorage> by DefaultMutableKrate(
    factory = {
        AntiSwearStorage(
            playerName = player.name,
            uuid = player.uniqueId.toString()
        )
    },
    saver = { value ->
        val file = File(folder, "${player.uniqueId}.json")
        stringFormat.writeIntoFile(value, file)
    },
    loader = {
        val file = File(folder, "${player.uniqueId}.json")
        stringFormat.parse<AntiSwearStorage>(file).getOrNull()
    },
)
