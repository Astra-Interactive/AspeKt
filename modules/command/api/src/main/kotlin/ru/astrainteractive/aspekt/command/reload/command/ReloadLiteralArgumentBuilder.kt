package ru.astrainteractive.aspekt.command.reload.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.aspekt.plugin.model.PluginPermission
import ru.astrainteractive.aspekt.plugin.model.PluginTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

internal class ReloadLiteralArgumentBuilder(
    private val lifecyclePlugin: Lifecycle,
    private val multiplatformCommand: MultiplatformCommand,
    translationKrate: CachedKrate<PluginTranslation>,
    private val commandExceptionHandler: CommandExceptionHandler
) {
    private val translation by translationKrate
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("aesreload") {
                runs(commandExceptionHandler::onException) { ctx ->
                    ctx.requirePermission(PluginPermission.RELOAD)
                    val audience = ctx.getSender()
                    audience.sendMessage(translation.reload.started)
                    lifecyclePlugin.onReload()
                    audience.sendMessage(translation.reload.completed)
                }
            }
        }
    }
}
