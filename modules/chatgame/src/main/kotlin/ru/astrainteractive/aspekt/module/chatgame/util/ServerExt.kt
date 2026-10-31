package ru.astrainteractive.aspekt.module.chatgame.util

import org.bukkit.Server
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.server.sendMessage
import ru.astrainteractive.astralibs.server.util.asKAudience

@Suppress("TrivialLambdaReference")
internal fun Server.broadcast(message: LocalizableComponent) {
    val receivers = onlinePlayers + consoleSender
    receivers
        .map { receiver -> receiver.asKAudience() }
        .sendMessage(message)
}
