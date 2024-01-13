package ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.command.api.Command
import ru.astrainteractive.astralibs.command.api.DefaultCommandFactory
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.util.StringListExt.withEntry
import ru.astrainteractive.klibs.kdi.Factory

internal class AdminPrivateCommandFactory(
    private val plugin: JavaPlugin,
    private val adminPrivateController: AdminPrivateController,
    private val scope: CoroutineScope,
    private val translation: PluginTranslation,
    private val dispatchers: BukkitDispatchers,
    private val kyoriComponentSerializer: KyoriComponentSerializer
) : Factory<AdminPrivateCommand> {

    private fun adminPrivateCompleter() =
        plugin.getCommand("adminprivate")?.setTabCompleter { sender, command, label, args ->
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

    private inner class AdminPrivateCommandImpl :
        AdminPrivateCommand,
        Command<AdminPrivateCommand.Output, AdminPrivateCommand.Input> by DefaultCommandFactory.create(
            alias = "adminprivate",
            commandParser = AdminPrivateCommandParser(),
            commandExecutor = AdminPrivateCommandExecutor(
                adminPrivateController = adminPrivateController,
                scope = scope,
                translation = translation,
                dispatchers = dispatchers,
                kyoriComponentSerializer = kyoriComponentSerializer
            ),
            resultHandler = { commandSender, result ->
                when (result) {
                    AdminPrivateCommand.Output.NoPermission -> with(kyoriComponentSerializer) {
                        commandSender.sendMessage(translation.general.noPermission.let(::toComponent))
                    }

                    AdminPrivateCommand.Output.NotPlayer -> with(kyoriComponentSerializer) {
                        commandSender.sendMessage(translation.general.onlyPlayerCommand.let(::toComponent))
                    }

                    AdminPrivateCommand.Output.WrongUsage -> with(kyoriComponentSerializer) {
                        commandSender.sendMessage(translation.general.wrongUsage.let(::toComponent))
                    }

                    else -> Unit
                }
            },
            mapper = {
                when (it) {
                    is AdminPrivateCommand.Output.Claim -> {
                        AdminPrivateCommand.Input.Claim(
                            player = it.player
                        )
                    }

                    is AdminPrivateCommand.Output.SetFlag -> {
                        AdminPrivateCommand.Input.SetFlag(
                            player = it.player,
                            flag = it.flag,
                            value = it.value
                        )
                    }

                    is AdminPrivateCommand.Output.ShowMap -> {
                        AdminPrivateCommand.Input.ShowMap(
                            player = it.player
                        )
                    }

                    is AdminPrivateCommand.Output.UnClaim -> {
                        AdminPrivateCommand.Input.UnClaim(
                            player = it.player
                        )
                    }

                    AdminPrivateCommand.Output.NoPermission,
                    AdminPrivateCommand.Output.NotPlayer,
                    AdminPrivateCommand.Output.WrongUsage -> null
                }
            }
        )

    override fun create(): AdminPrivateCommand {
        adminPrivateCompleter()
        return AdminPrivateCommandImpl().also {
            it.register(plugin)
        }
    }
}
