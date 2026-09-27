package ru.astrainteractive.aspekt.module.chatgame.service

import org.bukkit.Server
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.server.sendMessage
import ru.astrainteractive.astralibs.server.util.asKAudience

/** Like [Server.broadcast], but every player reads [message] in their own language; the console gets it too. */
internal fun Server.broadcast(message: LocalizableComponent) {
    val receivers = onlinePlayers + consoleSender
    receivers
        .map { receiver -> receiver.asKAudience() }
        .sendMessage(message)
}
