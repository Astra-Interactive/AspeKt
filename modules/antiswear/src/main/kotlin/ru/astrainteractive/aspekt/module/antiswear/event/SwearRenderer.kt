package ru.astrainteractive.aspekt.module.antiswear.event

import io.papermc.paper.chat.ChatRenderer
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.antiswear.util.SwearRuRegex

internal class SwearRenderer(private val renderer: ChatRenderer) : ChatRenderer {
    private fun Component.replaceSwears(): Component {
        val config = TextReplacementConfig.builder()
            .match(SwearRuRegex.regex.pattern)
            .replacement("****")
            .build()
        return replaceText(config)
        return replaceText { builder ->
            builder
                .match(SwearRuRegex.regex.pattern)
                .replacement("****")
//                .replacement { _, u ->
//                    val content = u.content()
//                    when {
//                        content.length <= 2 -> u.content("***")
//                        else -> {
//                            val length = content.length / 2
//                            val asterString = (0 until length).joinToString("") { "*" }
//                            val newContent = content.replaceRange(0, length, asterString)
//                            u.content(newContent)
//                        }
//                    }
//                }
        }
    }

    override fun render(
        source: Player,
        sourceDisplayName: Component,
        message: Component,
        viewer: Audience
    ): Component {
        val originalMessage = renderer.render(source, sourceDisplayName, message, viewer)
        val player = viewer as? Player ?: return originalMessage
        val restrictedPlayers = listOf("Tumka").map(String::lowercase)
        if (!restrictedPlayers.contains(player.name.lowercase())) return originalMessage
        return originalMessage.replaceSwears()
    }
}
