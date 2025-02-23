package ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate

import ru.astrainteractive.aspekt.module.adminprivate.command.di.AdminPrivateCommandDependencies
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.astralibs.command.api.exception.ArgumentTypeException
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException
import ru.astrainteractive.astralibs.command.api.exception.DefaultCommandException
import ru.astrainteractive.astralibs.command.api.exception.NoPermissionException
import ru.astrainteractive.astralibs.command.api.util.PluginExt.setCommandExecutor
import ru.astrainteractive.astralibs.util.StringListExt.withEntry

internal class AdminPrivateCommandRegistry(
    dependencies: AdminPrivateCommandDependencies
) : AdminPrivateCommandDependencies by dependencies {

    private fun adminPrivateCompleter() =
        plugin.getCommand("adminprivate")?.setTabCompleter { sender, command, label, args ->
            when {
                args.size <= 1 -> listOf("claim", "unclaim", "flag", "map").withEntry(args.getOrNull(0))
                args.getOrNull(0) == "flag" -> when (args.size) {
                    2 -> ChunkFlag.entries.map(ChunkFlag::toString).withEntry(args.getOrNull(1))
                    3 -> listOf("true", "false").withEntry(args.getOrNull(2))
                    else -> emptyList()
                }

                else -> emptyList()
            }
        }

    fun register() {
        adminPrivateCompleter()
        plugin.setCommandExecutor(
            alias = "adminprivate",
            commandParser = AdminPrivateCommandParser(),
            commandExecutor = AdminPrivateCommandExecutor(dependencies = this),
            errorHandler = { context, throwable ->
                when (throwable) {
                    is AdminPrivateCommand.Error.NotPlayer -> with(kyoriComponentSerializer) {
                        context.sender.sendMessage(translation.general.onlyPlayerCommand.component)
                    }

                    is DefaultCommandException -> with(kyoriComponentSerializer) {
                        when (throwable) {
                            is ArgumentTypeException -> {
                                context.sender.sendMessage(translation.general.wrongUsage.component)
                            }

                            is BadArgumentException -> {
                                context.sender.sendMessage(translation.general.wrongUsage.component)
                            }

                            is NoPermissionException -> {
                                context.sender.sendMessage(translation.general.noPermission.component)
                            }

                            else -> {
                                // todo
                            }
                        }
                    }
                }
            }
        )
    }
}
