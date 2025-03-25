package ru.astrainteractive.aspekt.module.jail.command.jail.tabcomplete

import ru.astrainteractive.aspekt.module.jail.command.JailCommandManager
import ru.astrainteractive.aspekt.module.jail.command.jail.model.JailArg
import ru.astrainteractive.aspekt.module.jail.command.tabcomplete.withArgument
import ru.astrainteractive.astralibs.command.api.argumenttype.EnumArgumentType
import ru.astrainteractive.astralibs.command.api.command.BukkitTabCompleter
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.argumentOrElse
import ru.astrainteractive.astralibs.command.api.util.PluginExt.setCommandTabCompleter

internal fun JailCommandManager.jailTabCompleter() = plugin.setCommandTabCompleter(
    alias = "jail",
    tabCompleter = BukkitTabCompleter { ctx ->
        ctx.withArgument(
            index = 0,
            hints = JailArg.entries.map(JailArg::value),
            block = {
                val arg = ctx.argumentOrElse(
                    index = 0,
                    type = EnumArgumentType(JailArg.entries),
                    default = { JailArg.LIST }
                )
                when (arg) {
                    JailArg.LIST -> ctx.withArgument(
                        index = 1,
                        hints = listOf()
                    )

                    JailArg.CREATE -> ctx.withArgument(
                        index = 1,
                        hints = listOf("JAIL_NAME")
                    )

                    JailArg.DELETE -> listOf("JAIL_NAME")
                    JailArg.INMATE -> ctx.withArgument(
                        index = 1,
                        hints = listOf("JAIL_NAME"),
                        block = {
                            ctx.withArgument(
                                index = 2,
                                hints = listOf("USER_NAME"),
                                block = {
                                    ctx.withArgument(
                                        index = 3,
                                        hints = listOf("TIME:1s,1m,1h10m")
                                    )
                                }
                            )
                        }
                    )

                    JailArg.FREE -> listOf("USER_NAME")
                }
            }
        ).orEmpty()
    }
)
