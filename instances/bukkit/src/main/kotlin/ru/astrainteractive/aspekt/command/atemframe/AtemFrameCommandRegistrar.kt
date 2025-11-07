package ru.astrainteractive.aspekt.command.atemframe

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.util.argument
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.requireArgument
import ru.astrainteractive.astralibs.command.api.util.requirePermission
import ru.astrainteractive.astralibs.command.api.util.requirePlayer
import ru.astrainteractive.astralibs.command.api.util.runs

class AtemFrameCommandRegistrar {

    private fun execute(
        player: Player,
        isVisible: Boolean = false,
        isFixed: Boolean = false,
        radius: Double = 20.0
    ) {
        val itemFrames = player.location.getNearbyEntitiesByType(ItemFrame::class.java, radius)
        player.sendMessage(
            "Params: isVisible: $isVisible; isFixed: $isFixed; radius: $radius; changed ${itemFrames.size} frames"
        )
        itemFrames.forEach {
            it.isFixed = isFixed
            it.isVisible = isVisible
        }
    }

    fun createNode(): LiteralCommandNode<CommandSourceStack> {
        return command("atemframe") {
            runs { ctx ->
                ctx.requirePermission(PluginPermission.ATEM_FRAME)
                execute(player = ctx.requirePlayer())
            }
            argument("isVisible", BoolArgumentType.bool()) { isVisibleArg ->
                runs { ctx ->
                    ctx.requirePermission(PluginPermission.ATEM_FRAME)
                    execute(
                        player = ctx.requirePlayer(),
                        isVisible = ctx.requireArgument(isVisibleArg)
                    )
                }
                argument("isFixed", BoolArgumentType.bool()) { isFixedArg ->
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.ATEM_FRAME)
                        execute(
                            player = ctx.requirePlayer(),
                            isVisible = ctx.requireArgument(isVisibleArg),
                            isFixed = ctx.requireArgument(isFixedArg)
                        )
                    }
                    argument("radius", DoubleArgumentType.doubleArg(0.0)) { radiusArg ->
                        runs { ctx ->
                            ctx.requirePermission(PluginPermission.ATEM_FRAME)
                            execute(
                                player = ctx.requirePlayer(),
                                isVisible = ctx.requireArgument(isVisibleArg),
                                isFixed = ctx.requireArgument(isFixedArg),
                                radius = ctx.requireArgument(radiusArg)
                            )
                        }
                    }
                }
            }
        }.build()
    }
}
