package ru.astrainteractive.aspekt.command.rtp

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

/**
 * RTP command registrar. Builds Brigadier node for:
 * /rtp
 */
class RtpLiteralArgumentBuilder(
    private val multiplatformCommand: MultiplatformCommand,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<PluginTranslation>,
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("rtp") {
                runs { ctx ->
                    ctx.getSender().sendMessage(translation.general.maybeTpr.component)
                }
            }
        }
    }
}
