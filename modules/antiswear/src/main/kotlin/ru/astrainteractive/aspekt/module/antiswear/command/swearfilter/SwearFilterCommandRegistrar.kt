package ru.astrainteractive.aspekt.module.antiswear.command.swearfilter

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepository
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.argumenttype.OnlinePlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.util.argument
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.hints
import ru.astrainteractive.astralibs.command.api.util.requireArgument
import ru.astrainteractive.astralibs.command.api.util.requirePlayer
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

internal class SwearFilterCommandRegistrar(
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val ioScope: CoroutineScope,
    private val swearRepository: SwearRepository
) {
    private val translation by translationKrate
    private val kyori by kyoriKrate

    private fun execute(
        sender: CommandSender,
        target: Player,
        isEnabled: Boolean
    ) {
        ioScope.launch { swearRepository.setSwearFilterEnabled(target, isEnabled) }
        if (isEnabled) {
            if (sender != target) {
                translation.swear.swearFilterEnabledFor(target.name)
                    .let(kyori::toComponent)
                    .run(sender::sendMessage)
            }
            translation.swear.swearFilterEnabled
                .let(kyori::toComponent)
                .run(target::sendMessage)
        } else {
            if (sender != target) {
                translation.swear.swearFilterDisabledFor(target.name)
                    .let(kyori::toComponent)
                    .run(sender::sendMessage)
            }
            translation.swear.swearFilterDisabled
                .let(kyori::toComponent)
                .run(target::sendMessage)
        }
    }

    fun createNode(): LiteralCommandNode<CommandSourceStack> {
        return command("swearfilter") {
            argument("on_off", StringArgumentType.string()) { onOffArg ->
                hints { listOf("on", "off") }
                runs { ctx ->
                    val playerSender: Player = ctx.requirePlayer()
                    val value = ctx.requireArgument(onOffArg, OnOffArgumentConverter)
                    execute(playerSender, playerSender, value)
                }
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                    runs { ctx ->
                        val sender = ctx.source.sender
                        val targetPlayer = ctx.requireArgument(playerArg, OnlinePlayerArgumentConverter)
                        val value = ctx.requireArgument(onOffArg, OnOffArgumentConverter)
                        execute(sender, targetPlayer, value)
                    }
                }
            }
        }.build()
    }
}
