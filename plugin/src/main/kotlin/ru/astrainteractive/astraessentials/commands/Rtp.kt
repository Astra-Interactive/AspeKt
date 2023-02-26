package ru.astrainteractive.astraessentials.commands

import ru.astrainteractive.astraessentials.AstraEssentials
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.utils.HEX

fun CommandManager.rtp() = AstraEssentials.instance.registerCommand("rtp") {
    sender.sendMessage("#db2c18Возможно, вы хотели ввести /tpr".HEX())
}