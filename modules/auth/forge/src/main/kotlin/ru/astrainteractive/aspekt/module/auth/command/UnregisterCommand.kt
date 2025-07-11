package ru.astrainteractive.aspekt.module.auth.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.model.PlayerLoginModel
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthPermission
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.command.util.argument
import ru.astrainteractive.astralibs.command.util.command
import ru.astrainteractive.astralibs.command.util.hints
import ru.astrainteractive.astralibs.command.util.requireArgument
import ru.astrainteractive.astralibs.command.util.requirePermission
import ru.astrainteractive.astralibs.command.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.server.util.ForgeUtil
import ru.astrainteractive.astralibs.server.util.asAudience
import ru.astrainteractive.astralibs.server.util.getOnlinePlayer
import ru.astrainteractive.astralibs.server.util.getOnlinePlayers
import ru.astrainteractive.astralibs.server.util.getPlayerGameProfile
import ru.astrainteractive.astralibs.server.util.toPlain
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

fun RegisterCommandsEvent.unregisterCommand(
    scope: CoroutineScope,
    authDao: AuthDao,
    authorizedApi: AuthorizedApi,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<AuthTranslation>
) {
    command("unregister") {
        argument("username", com.mojang.brigadier.arguments.StringArgumentType.string()) {
            hints(ForgeUtil.getOnlinePlayers().map { player -> player.name.toPlain() })
            runs { ctx ->
                val translation = translationKrate.cachedValue
                ctx.requirePermission(AuthPermission.Unregister)
                val usernameToDelete = ctx.requireArgument("username", StringArgumentType)
                scope.launch {
                    with(kyoriKrate.cachedValue) {
                        val authData = authDao.getUser(usernameToDelete)
                            .onFailure {
                                ctx.source
                                    .asAudience()
                                    .sendMessage(translation.userNotFound.component)
                            }.getOrNull() ?: run { return@launch }

                        authDao.deleteAccount(authData.uuid)
                            .onSuccess {
                                val onlinePlayerLoginModel = ForgeUtil
                                    .getOnlinePlayer(usernameToDelete)
                                    ?.let { player ->
                                        PlayerLoginModel(
                                            username = player.name.toPlain(),
                                            uuid = player.uuid,
                                            ip = player.ipAddress
                                        )
                                    }
                                val offlinePlayerLoginModel = ForgeUtil
                                    .getPlayerGameProfile(usernameToDelete)
                                    ?.let { profile ->
                                        PlayerLoginModel(
                                            username = profile.name,
                                            uuid = profile.id,
                                            ip = null.orEmpty()
                                        )
                                    }
                                val playerLoginModel = onlinePlayerLoginModel ?: offlinePlayerLoginModel
                                playerLoginModel
                                    ?.uuid
                                    ?.let { playerLoginModel ->
                                        authorizedApi.forgetUser(playerLoginModel)
                                        ctx.source
                                            .asAudience()
                                            .sendMessage(translation.userDeleted.component)
                                    }
                                onlinePlayerLoginModel
                                    ?.let(authorizedApi::loadUserInfo)
                            }.onFailure {
                                ctx.source
                                    .asAudience()
                                    .sendMessage(translation.userCouldNotBeDeleted.component)
                            }
                    }
                }
            }
        }
    }.run(dispatcher::register)
}
