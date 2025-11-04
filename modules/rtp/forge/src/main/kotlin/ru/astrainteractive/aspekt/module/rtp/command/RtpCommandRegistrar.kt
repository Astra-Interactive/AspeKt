package ru.astrainteractive.aspekt.module.rtp.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.level.ServerPlayer
import ru.astrainteractive.astralibs.command.util.command
import ru.astrainteractive.astralibs.command.util.runs
import ru.astrainteractive.astralibs.server.util.ForgeUtil
import ru.astrainteractive.astralibs.server.util.asOnlineMinecraftPlayer
import ru.astrainteractive.astralibs.server.util.getNextTickTime
import ru.astrainteractive.klibs.mikro.core.util.tryCast

/**
 * Forge RTP command registrar. Builds and registers Brigadier node for:
 * /rtp
 */
class RtpCommandRegistrar(
    private val executor: RtpCommandExecutor
) {
    fun createNode(): LiteralArgumentBuilder<CommandSourceStack> {
        return command(alias = "rtp") {
            runs { ctx ->
                val player = ctx.source.player?.tryCast<ServerPlayer>() ?: return@runs
                RtpCommand(
                    player = player.asOnlineMinecraftPlayer(),
                    nextTickTime = ForgeUtil.getNextTickTime()
                ).run(executor::execute)
            }
        }
    }
}
