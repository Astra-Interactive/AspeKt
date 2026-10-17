package ru.astrainteractive.aspekt.module.auth.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.dao.api.AuthRepository
import ru.astrainteractive.aspekt.module.auth.model.AuthPermission
import ru.astrainteractive.aspekt.module.auth.model.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.model.PlayerLoginModel
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

/**
 * /unregister <username>
 */
internal class UnregisterCommandRegistrar(
    translationKrate: CachedKrate<AuthTranslation>,
    private val ioScope: CoroutineScope,
    private val authRepository: AuthRepository,
    private val authorizedApi: AuthorizedApi,
    private val platformServer: PlatformServer,
    private val multiplatformCommand: MultiplatformCommand,
    private val commandExceptionHandler: CommandExceptionHandler
) {
    private val translation by translationKrate

    @Suppress("LongMethod")
    private fun createNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("unregister") {
                argument(
                    alias = "username",
                    type = StringArgumentType.string(),
                    block = { usernameArg ->
                        hints { platformServer.getOnlinePlayers().map { it.name } }
                        runs(commandExceptionHandler::handle) { ctx ->
                            ctx.requirePermission(AuthPermission.Unregister)
                            val usernameToDelete = ctx.requireArgument(usernameArg)
                            ioScope.launch {
                                val authData = authRepository.getUser(usernameToDelete)
                                    .onFailure {
                                        ctx.getSender().sendMessage(translation.userNotFound)
                                    }.getOrNull() ?: return@launch

                                authRepository.deleteAccount(authData.uuid)
                                    .onSuccess {
                                        val onlinePlayer = platformServer.findOnlinePlayer(usernameToDelete)
                                        val onlinePlayerLoginModel = onlinePlayer?.let { player ->
                                            PlayerLoginModel(
                                                username = player.name,
                                                uuid = player.uuid,
                                                ip = player.address.hostName
                                            )
                                        }
                                        val offlinePlayerLoginModel = platformServer
                                            .findOfflinePlayer(usernameToDelete)
                                            ?.let { profile ->
                                                PlayerLoginModel(
                                                    username = profile.name ?: usernameToDelete,
                                                    uuid = profile.uuid,
                                                    ip = ""
                                                )
                                            }
                                        val playerLoginModel = onlinePlayerLoginModel ?: offlinePlayerLoginModel
                                        playerLoginModel
                                            ?.uuid
                                            ?.let { uuid ->
                                                authorizedApi.forgetUser(uuid)
                                                ctx.getSender().sendMessage(translation.userDeleted)
                                            }
                                        onlinePlayerLoginModel
                                            ?.let(authorizedApi::loadUserInfo)
                                    }.onFailure {
                                        ctx.getSender().sendMessage(translation.userCouldNotBeDeleted)
                                    }
                            }
                        }
                    }
                )
            }
        }
    }

    fun createNodes(): List<LiteralArgumentBuilder<Any>> {
        return listOf(createNode())
    }
}
