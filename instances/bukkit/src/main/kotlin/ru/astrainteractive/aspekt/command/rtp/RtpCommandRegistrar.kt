package ru.astrainteractive.aspekt.command.rtp

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

/**
 * RTP command registrar. Builds Brigadier node for:
 * /rtp
 */
class RtpCommandRegistrar(
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
) {
    private val translation by translationKrate
    private val kyori by kyoriKrate
    fun createNode(): LiteralCommandNode<CommandSourceStack> {
        return command("rtp") {
            runs { ctx ->
                val sender = ctx.source.sender
                translation.general.maybeTpr
                    .let(kyori::toComponent)
                    .run(sender::sendMessage)
            }
        }.build()
    }
}
