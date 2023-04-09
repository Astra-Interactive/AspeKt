package ru.astrainteractive.aspekt.commands

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.utils.HEX

fun CommandManager.rtp() = AspeKt.instance.registerCommand("rtp") {
    sender.sendMessage("#db2c18Возможно, вы хотели ввести /tpr".HEX())
}