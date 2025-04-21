package ru.astrainteractive.aspekt.module.claims.command

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import ru.astrainteractive.aspekt.core.forge.command.util.argument
import ru.astrainteractive.aspekt.core.forge.command.util.command
import ru.astrainteractive.aspekt.core.forge.command.util.hints
import ru.astrainteractive.aspekt.core.forge.command.util.literal
import ru.astrainteractive.aspekt.core.forge.command.util.runs
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayers
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag

fun claim2Command(): LiteralArgumentBuilder<CommandSourceStack?> {
    return command("claim2") {
        literal("flag") {
            argument("flag_type", StringArgumentType.string()) {
                hints(ChunkFlag.entries.map(ChunkFlag::name))
                argument("bool", BoolArgumentType.bool()) {
                    runs {
                        println("run claim2 flag type bool")
                    }
                }
            }
        }
        literal("add") {
            argument("player", StringArgumentType.string()) {
                hints(ForgeUtil.getOnlinePlayers().map { player -> player.name.toPlain() })
                runs {
                    println("add player")
                }
            }
        }
        literal("remove") {
            argument("player", StringArgumentType.string()) {
                hints(ForgeUtil.getOnlinePlayers().map { player -> player.name.toPlain() })
                runs {
                    println("remove player")
                }
            }
        }
        literal("map") {
            runs {
                println("map")
            }
        }
        literal("claim") {
            runs {
                println("claim")
            }
        }
        literal("unclaim") {
            runs {
                println("claim")
            }
        }
    }
}
