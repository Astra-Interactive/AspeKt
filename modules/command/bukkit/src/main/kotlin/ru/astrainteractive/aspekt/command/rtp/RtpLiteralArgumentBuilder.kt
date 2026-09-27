package ru.astrainteractive.aspekt.command.rtp

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

/**
 * RTP command registrar. Builds Brigadier node for:
 * /rtp
 */
internal class RtpLiteralArgumentBuilder(
    private val multiplatformCommand: MultiplatformCommand,
    translationKrate: CachedKrate<PluginTranslation>,
) {
    private val translation by translationKrate
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("rtp") {
                runs { ctx ->
                    ctx.getSender().sendMessage(translation.general.maybeTpr)
                }
            }
        }
    }
}
