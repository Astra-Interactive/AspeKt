package ru.astrainteractive.aspekt.module.jail.command.jail.tabcomplete

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.jail.command.JailCommandManager
import ru.astrainteractive.aspekt.module.jail.command.jail.model.JailArg
import ru.astrainteractive.aspekt.module.jail.command.tabcomplete.withArgument
import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
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
                        hints = cachedJailApi.getJails().map(Jail::name)
                    )

                    JailArg.DELETE -> cachedJailApi.getJails().map(Jail::name)
                    JailArg.INMATE -> ctx.withArgument(
                        index = 1,
                        hints = cachedJailApi.getJails().map(Jail::name),
                        block = {
                            ctx.withArgument(
                                index = 2,
                                hints = Bukkit.getOnlinePlayers().map(Player::getName),
                                block = {
                                    ctx.withArgument(
                                        index = 3,
                                        hints = listOf("TIME:1s,1m,1h10m")
                                    )
                                }
                            )
                        }
                    )

                    JailArg.FREE -> cachedJailApi.getInmates().map(JailInmate::lastUsername)
                }
            }
        ).orEmpty()
    }
)
