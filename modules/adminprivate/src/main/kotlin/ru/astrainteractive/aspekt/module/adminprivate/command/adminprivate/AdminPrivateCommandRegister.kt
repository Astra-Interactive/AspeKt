package ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.command.api.commandfactory.BukkitCommandFactory
import ru.astrainteractive.astralibs.command.api.registry.BukkitCommandRegistry
import ru.astrainteractive.astralibs.command.api.registry.BukkitCommandRegistryContext.Companion.toCommandRegistryContext
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.util.StringListExt.withEntry

internal class AdminPrivateCommandRegister(
    private val plugin: JavaPlugin,
    private val adminPrivateController: AdminPrivateController,
    private val scope: CoroutineScope,
    private val translation: PluginTranslation,
    private val dispatchers: BukkitDispatchers,
    private val kyoriComponentSerializer: KyoriComponentSerializer
) {

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

    fun register() {
        adminPrivateCompleter()
        val command = BukkitCommandFactory.create(
            alias = "adminprivate",
            commandParser = AdminPrivateCommandParser(),
            commandExecutor = AdminPrivateCommandExecutor(
                adminPrivateController = adminPrivateController,
                scope = scope,
                translation = translation,
                dispatchers = dispatchers,
                kyoriComponentSerializer = kyoriComponentSerializer
            ),
            commandSideEffect = { context, result ->
                when (result) {
                    AdminPrivateCommand.Output.NoPermission -> with(kyoriComponentSerializer) {
                        context.sender.sendMessage(translation.general.noPermission.let(::toComponent))
                    }

                    AdminPrivateCommand.Output.NotPlayer -> with(kyoriComponentSerializer) {
                        context.sender.sendMessage(translation.general.onlyPlayerCommand.let(::toComponent))
                    }

                    AdminPrivateCommand.Output.WrongUsage -> with(kyoriComponentSerializer) {
                        context.sender.sendMessage(translation.general.wrongUsage.let(::toComponent))
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
        BukkitCommandRegistry.register(command, plugin.toCommandRegistryContext())
    }
}
