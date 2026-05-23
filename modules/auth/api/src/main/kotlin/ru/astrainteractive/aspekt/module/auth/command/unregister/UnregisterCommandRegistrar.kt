package ru.astrainteractive.aspekt.module.auth.command.unregister

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.model.PlayerLoginModel
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthPermission
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

/**
 * /unregister <username>
 */
class UnregisterCommandRegistrar(
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<AuthTranslation>,
    private val ioScope: CoroutineScope,
    private val authDao: AuthDao,
    private val authorizedApi: AuthorizedApi,
    private val platformServer: PlatformServer,
    private val multiplatformCommand: MultiplatformCommand,
    private val registrarContext: CommandRegistrarContext
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
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
                        runs { ctx ->
                            ctx.requirePermission(AuthPermission.Unregister)
                            val usernameToDelete = ctx.requireArgument(usernameArg)
                            ioScope.launch {
                                val authData = authDao.getUser(usernameToDelete)
                                    .onFailure {
                                        ctx.getSender().sendMessage(translation.userNotFound.component)
                                    }.getOrNull() ?: return@launch

                                authDao.deleteAccount(authData.uuid)
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
                                                ctx.getSender().sendMessage(translation.userDeleted.component)
                                            }
                                        onlinePlayerLoginModel
                                            ?.let(authorizedApi::loadUserInfo)
                                    }.onFailure {
                                        ctx.getSender().sendMessage(translation.userCouldNotBeDeleted.component)
                                    }
                            }
                        }
                    }
                )
            }
        }
    }

    fun register() {
        registrarContext.registerWhenReady(createNode())
    }
}
