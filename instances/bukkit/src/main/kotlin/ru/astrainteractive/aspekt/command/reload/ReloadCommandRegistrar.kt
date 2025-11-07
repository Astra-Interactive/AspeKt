package ru.astrainteractive.aspekt.command.reload

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.requirePermission
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

class ReloadCommandRegistrar(
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val plugin: LifecyclePlugin
) {
    private val translation by translationKrate
    private val kyori by kyoriKrate
    fun createNode(): LiteralCommandNode<CommandSourceStack> {
        return command("aesreload") {
            runs { ctx ->
                ctx.requirePermission(PluginPermission.RELOAD)
                val sender = ctx.source.sender
                translation.general.reload
                    .let(kyori::toComponent)
                    .run(sender::sendMessage)
                plugin.onReload()
                translation.general.reloadComplete
                    .let(kyori::toComponent)
                    .run(sender::sendMessage)
            }
        }.build()
    }
}
