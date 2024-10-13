package ru.astrainteractive.aspekt.module.antiswear.event

import io.papermc.paper.chat.ChatRenderer
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepository
import ru.astrainteractive.aspekt.module.antiswear.util.SwearRuRegex
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger

internal class SwearRenderer(
    private val renderer: ChatRenderer,
    private val swearRepository: SwearRepository
) : ChatRenderer, Logger by JUtiltLogger("AspeKt-SwearRenderer") {
    override fun render(
        source: Player,
        sourceDisplayName: Component,
        message: Component,
        viewer: Audience
    ): Component {
        val originalMessage = renderer.render(source, sourceDisplayName, message, viewer)
        val player = viewer as? Player ?: return originalMessage
        if (!swearRepository.isSwearFilterEnabled(player)) return originalMessage
        val newText = originalMessage.replaceText(SwearRuRegex.REPLACEMENT_CONFIG)

        info {
            "#render swear for player is disabled. rendering: ${
                PlainTextComponentSerializer.plainText().serialize(originalMessage)
            } -> ${PlainTextComponentSerializer.plainText().serialize(newText)}."
        }
        return newText
    }
}
