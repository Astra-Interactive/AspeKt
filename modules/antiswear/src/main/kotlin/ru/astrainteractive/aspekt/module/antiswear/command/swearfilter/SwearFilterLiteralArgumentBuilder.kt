package ru.astrainteractive.aspekt.module.antiswear.command.swearfilter

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepository
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.argumenttype.OnlineKPlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.brigadier.sender.KCommandSender
import ru.astrainteractive.astralibs.command.api.brigadier.sender.KPlayerKCommandSender
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.util.tryCast

internal class SwearFilterLiteralArgumentBuilder(
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val ioScope: CoroutineScope,
    private val swearRepository: SwearRepository,
    private val multiplatformCommand: MultiplatformCommand,
    private val platformServer: PlatformServer
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate

    private fun execute(
        sender: KCommandSender,
        target: OnlineKPlayer,
        isEnabled: Boolean
    ) {
        val senderUuidOrNull = sender
            .tryCast<KPlayerKCommandSender>()
            ?.instance
            ?.uuid
        ioScope.launch { swearRepository.setSwearFilterEnabled(target, isEnabled) }
        if (isEnabled) {
            if (senderUuidOrNull != target.uuid) {
                sender.sendMessage(translation.swear.swearFilterEnabledFor(target.name).component)
            }
            target.sendMessage(translation.swear.swearFilterEnabled.component)
        } else {
            if (senderUuidOrNull != target.uuid) {
                sender.sendMessage(translation.swear.swearFilterDisabledFor(target.name).component)
            }
            target.sendMessage(translation.swear.swearFilterDisabled.component)
        }
    }

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("swearfilter") {
                argument("on_off", StringArgumentType.string()) { onOffArg ->
                    hints { listOf("on", "off") }
                    runs { ctx ->
                        val value = ctx.requireArgument(onOffArg, OnOffArgumentConverter)
                        execute(
                            sender = ctx.getSender(),
                            target = ctx.requirePlayer(),
                            isEnabled = value
                        )
                    }
                    argument("player", StringArgumentType.string()) { playerArg ->
                        hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                        runs { ctx ->
                            val targetPlayer = ctx.requireArgument(
                                bArgument = playerArg,
                                converter = OnlineKPlayerArgumentConverter(platformServer)
                            )
                            val value = ctx.requireArgument(onOffArg, OnOffArgumentConverter)
                            execute(ctx.getSender(), targetPlayer, value)
                        }
                    }
                }
            }
        }
    }
}
