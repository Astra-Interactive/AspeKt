package ru.astrainteractive.aspekt.module.claims.command.claim

import ru.astrainteractive.aspekt.module.claims.command.di.ClaimCommandDependencies
import ru.astrainteractive.aspekt.module.claims.messenger.BukkitMessenger
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.astralibs.command.api.exception.ArgumentTypeException
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException
import ru.astrainteractive.astralibs.command.api.exception.DefaultCommandException
import ru.astrainteractive.astralibs.command.api.exception.NoPermissionException
import ru.astrainteractive.astralibs.command.api.util.PluginExt.setCommandExecutor
import ru.astrainteractive.astralibs.util.StringListExt.withEntry

internal class ClaimCommandRegistry(
    private val dependencies: ClaimCommandDependencies
) : ClaimCommandDependencies by dependencies {

    private fun claimCompleter() =
        plugin.getCommand("claim")?.setTabCompleter { sender, command, label, args ->
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
        claimCompleter()
        plugin.setCommandExecutor(
            alias = "claim",
            commandParser = ClaimCommandParser(),
            commandExecutor = ClaimCommandExecutor(
                messenger = BukkitMessenger(kyoriKrate = dependencies.kyoriComponentSerializer),
                scope = dependencies.scope,
                dispatchers = dependencies.dispatchers,
                translationKrate = dependencies.translation,
                claimsRepository = dependencies.claimsRepository,
                claimErrorMapper = dependencies.claimErrorMapper
            ),
            errorHandler = { context, throwable ->
                when (throwable) {
                    is Claimommand.Error.NotPlayer -> with(kyoriComponentSerializer.cachedValue) {
                        context.sender.sendMessage(translation.cachedValue.general.onlyPlayerCommand.component)
                    }

                    is DefaultCommandException -> with(kyoriComponentSerializer.cachedValue) {
                        when (throwable) {
                            is ArgumentTypeException -> {
                                context.sender.sendMessage(translation.cachedValue.general.wrongUsage.component)
                            }

                            is BadArgumentException -> {
                                context.sender.sendMessage(translation.cachedValue.general.wrongUsage.component)
                            }

                            is NoPermissionException -> {
                                context.sender.sendMessage(translation.cachedValue.general.noPermission.component)
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
