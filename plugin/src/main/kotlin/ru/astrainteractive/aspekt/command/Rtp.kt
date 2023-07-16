package ru.astrainteractive.aspekt.command

import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.utils.hex

fun CommandManager.rtp() = plugin.registerCommand("rtp") {
    sender.sendMessage("#db2c18Возможно, вы хотели ввести /tpr".hex())
}
