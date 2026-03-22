package ru.astrainteractive.aspekt.command.reload

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

class ReloadLiteralArgumentBuilder(
    private val plugin: LifecyclePlugin,
    private val multiplatformCommand: MultiplatformCommand,
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("aesreload") {
                runs { ctx ->
                    ctx.requirePermission(PluginPermission.RELOAD)
                    val audience = ctx.getSender()
                    audience.sendMessage(translation.general.reload.component)
                    plugin.onReload()
                    audience.sendMessage(translation.general.reloadComplete.component)
                }
            }
        }
    }
}
