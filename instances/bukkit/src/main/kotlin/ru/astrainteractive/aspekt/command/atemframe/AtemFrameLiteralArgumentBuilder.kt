package ru.astrainteractive.aspekt.command.atemframe

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.kyori.adventure.text.Component
import org.bukkit.entity.ItemFrame
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.astralibs.server.util.asBukkitLocation

class AtemFrameLiteralArgumentBuilder(
    private val multiplatformCommand: MultiplatformCommand
) {

    private fun execute(
        player: OnlineKPlayer,
        isVisible: Boolean = false,
        isFixed: Boolean = false,
        radius: Double = 20.0
    ) {
        val itemFrames = player.getLocation()
            .asBukkitLocation()
            .getNearbyEntitiesByType(ItemFrame::class.java, radius)
        player.sendMessage(
            Component.text(
                "Params: isVisible: $isVisible; isFixed: $isFixed; radius: $radius; changed ${itemFrames.size} frames"
            )
        )
        itemFrames.forEach { itemFrame ->
            itemFrame.isFixed = isFixed
            itemFrame.isVisible = isVisible
        }
    }

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("atemframe") {
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
            }
        }
    }
}
