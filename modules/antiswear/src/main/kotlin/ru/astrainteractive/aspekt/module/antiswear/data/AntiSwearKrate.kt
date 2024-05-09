package ru.astrainteractive.aspekt.module.antiswear.data

import kotlinx.serialization.StringFormat
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.antiswear.model.AntiSwearStorage
import ru.astrainteractive.astralibs.krate.core.FileKrate
import ru.astrainteractive.astralibs.krate.core.StringFormatKrate
import java.io.File

internal class AntiSwearKrate(
    player: Player,
    stringFormat: StringFormat,
) : FileKrate<AntiSwearStorage> by StringFormatKrate(
    default = AntiSwearStorage(
        playerName = player.name,
        uuid = player.uniqueId.toString()
    ),
    fileName = "${player.uniqueId}.json",
    stringFormat = stringFormat,
    kSerializer = AntiSwearStorage.serializer(),
    folder = File("./.temp/antiswear")
)
