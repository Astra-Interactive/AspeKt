package ru.astrainteractive.aspekt.module.antiswear.event

import io.papermc.paper.chat.ChatRenderer
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepository
import ru.astrainteractive.aspekt.module.antiswear.util.SwearRuRegex

internal class SwearRenderer(
    private val renderer: ChatRenderer,
    private val swearRepository: SwearRepository
) : ChatRenderer {
    private fun Component.replaceSwears(): Component {
        val config = TextReplacementConfig.builder()
            .match(SwearRuRegex.regex.pattern)
            .replacement("****")
            .build()
        return replaceText(config)
    }

    override fun render(
        source: Player,
        sourceDisplayName: Component,
        message: Component,
        viewer: Audience
    ): Component {
        val originalMessage = renderer.render(source, sourceDisplayName, message, viewer)
        val player = viewer as? Player ?: return originalMessage
        if (!swearRepository.isSwearFilterEnabled(player)) return originalMessage
        return originalMessage.replaceSwears()
    }
}
