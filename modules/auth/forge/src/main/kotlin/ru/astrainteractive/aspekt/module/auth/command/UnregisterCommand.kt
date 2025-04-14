package ru.astrainteractive.aspekt.module.auth.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.literal
import ru.astrainteractive.aspekt.core.forge.command.util.requireArgument
import ru.astrainteractive.aspekt.core.forge.command.util.requirePermission
import ru.astrainteractive.aspekt.core.forge.command.util.stringArgument
import ru.astrainteractive.aspekt.core.forge.kyori.sendSystemMessage
import ru.astrainteractive.aspekt.core.forge.kyori.withAudience
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayers
import ru.astrainteractive.aspekt.core.forge.util.getPlayerGameProfile
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.model.PlayerLoginModel
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthPermission
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.Krate

fun RegisterCommandsEvent.unregisterCommand(
    scope: CoroutineScope,
    authDao: AuthDao,
    authorizedApi: AuthorizedApi,
    kyoriKrate: Krate<KyoriComponentSerializer>,
    translationKrate: Krate<AuthTranslation>
) {
    literal("unregister") {
        stringArgument(
            alias = "username",
            suggests = ForgeUtil.getOnlinePlayers().map { player -> player.name.toPlain() },
            execute = execute@{ ctx ->
                val translation = translationKrate.cachedValue
                ctx.requirePermission(AuthPermission.Unregister)
                val usernameToDelete = ctx.requireArgument("username", StringArgumentType)
                scope.launch {
                    val authData = authDao.getUser(usernameToDelete)
                        .onFailure {
                            kyoriKrate
                                .withAudience(ctx.source)
                                .sendSystemMessage(translation.userNotFound)
                        }.getOrNull() ?: run {
                        return@launch
                    }

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
                                    kyoriKrate
                                        .withAudience(ctx.source)
                                        .sendSystemMessage(translation.userDeleted)
                                }
                            onlinePlayerLoginModel
                                ?.let(authorizedApi::loadUserInfo)
                        }.onFailure {
                            kyoriKrate
                                .withAudience(ctx.source)
                                .sendSystemMessage(translation.userCouldNotBeDeleted)
                        }
                }
            }
        )
    }.run(dispatcher::register)
}
