package ru.astrainteractive.aspekt.command.rtp.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.aspekt.plugin.model.PluginTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

internal class RtpLiteralArgumentBuilder(
    private val multiplatformCommand: MultiplatformCommand,
    translationKrate: CachedKrate<PluginTranslation>,
    private val commandExceptionHandler: CommandExceptionHandler,
) {
    private val translation by translationKrate
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("rtp") {
                runs(commandExceptionHandler::onException) { ctx ->
                    ctx.getSender().sendMessage(translation.rtp.didYouMeanTpr)
                }
            }
        }
    }
}
