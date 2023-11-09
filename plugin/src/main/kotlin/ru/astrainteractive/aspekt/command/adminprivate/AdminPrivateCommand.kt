package ru.astrainteractive.aspekt.command.adminprivate

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.adminprivate.model.ChunkFlag
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.command.api.Command
import ru.astrainteractive.astralibs.command.registerTabCompleter
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astralibs.util.withEntry

class AdminPrivateCommand(
    private val adminPrivateController: AdminPrivateController,
    private val scope: CoroutineScope,
    private val translation: PluginTranslation,
    private val dispatchers: BukkitDispatchers,
    translationContext: BukkitTranslationContext
) : Command, BukkitTranslationContext by translationContext {
    private val executor = AdminPrivateCommandExecutor(
        adminPrivateController = adminPrivateController,
        scope = scope,
        translation = translation,
        dispatchers = dispatchers,
        translationContext = translationContext
    )
    private val commandParser = AdminPrivateCommandParser()
    private fun adminPrivateCompleter(plugin: JavaPlugin) = plugin.registerTabCompleter("adminprivate") {
        when {
            args.size <= 1 -> listOf("claim", "unclaim", "flag", "map").withEntry(args.getOrNull(0))
            args.getOrNull(0) == "flag" -> when (args.size) {
                2 -> ChunkFlag.values().map(ChunkFlag::toString).withEntry(args.getOrNull(1))
                3 -> listOf("true", "false").withEntry(args.getOrNull(2))
                else -> emptyList()
            }

            else -> emptyList()
        }
    }

    override fun register(plugin: JavaPlugin) {
        adminPrivateCompleter(plugin)
        Command.registerDefault(
            plugin = plugin,
            commandParser = commandParser,
            commandExecutor = executor,
            resultHandler = { commandSender, result ->
                when (result) {
                    AdminPrivateCommandParser.Output.NoPermission -> {
                        commandSender.sendMessage(translation.noPermission)
                    }

                    AdminPrivateCommandParser.Output.NotPlayer -> {
                        commandSender.sendMessage(translation.onlyPlayerCommand)
                    }

                    AdminPrivateCommandParser.Output.WrongUsage -> {
                        commandSender.sendMessage(translation.wrongUsage)
                    }

                    else -> Unit
                }
            },
            transform = {
                when (it) {
                    is AdminPrivateCommandParser.Output.Claim -> {
                        AdminPrivateCommandExecutor.Input.Claim(
                            player = it.player
                        )
                    }

                    is AdminPrivateCommandParser.Output.SetFlag -> {
                        AdminPrivateCommandExecutor.Input.SetFlag(
                            player = it.player,
                            flag = it.flag,
                            value = it.value
                        )
                    }

                    is AdminPrivateCommandParser.Output.ShowMap -> {
                        AdminPrivateCommandExecutor.Input.ShowMap(
                            player = it.player
                        )
                    }

                    is AdminPrivateCommandParser.Output.UnClaim -> {
                        AdminPrivateCommandExecutor.Input.UnClaim(
                            player = it.player
                        )
                    }

                    AdminPrivateCommandParser.Output.NoPermission,
                    AdminPrivateCommandParser.Output.NotPlayer,
                    AdminPrivateCommandParser.Output.WrongUsage -> null
                }
            }
        )
    }
}
