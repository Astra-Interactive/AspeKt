package ru.astrainteractive.aspekt.command.rtpbypass

import com.earth2me.essentials.Essentials
import com.earth2me.essentials.RandomTeleport
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.OnlinePlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.klibs.mikro.core.util.tryCast

/**
 * RTP Bypassed command registrar. Builds Brigadier node for:
 * /rtpbypass <player>
 */
class RtpBypassLiteralArgumentBuilder(
    private val multiplatformCommand: MultiplatformCommand
) {
    private val essentials: Essentials?
        get() = Bukkit.getPluginManager()
            .getPlugin("Essentials")
            ?.tryCast<Essentials>()

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("rtpbypass") {
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.RTP_BYPASS)
                        val player = ctx.requireArgument(playerArg, OnlinePlayerArgumentConverter)
                        val randomTeleport = RandomTeleport(essentials)
                        val completable = randomTeleport.getRandomLocation(
                            randomTeleport.getCenter("default"),
                            randomTeleport.getMinRange("default"),
                            randomTeleport.getMaxRange("default")
                        )
                        completable.whenComplete { location, _ ->
                            player.teleport(location)
                        }
                    }
                }
            }
        }
    }
}
