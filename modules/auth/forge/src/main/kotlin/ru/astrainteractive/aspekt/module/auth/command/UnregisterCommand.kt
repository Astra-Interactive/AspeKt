package ru.astrainteractive.aspekt.module.auth.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.command
import ru.astrainteractive.aspekt.core.forge.command.util.requireArgument
import ru.astrainteractive.aspekt.core.forge.command.util.requirePermission
import ru.astrainteractive.aspekt.core.forge.command.util.stringArgument
import ru.astrainteractive.aspekt.core.forge.kyori.sendSystemMessage
import ru.astrainteractive.aspekt.core.forge.kyori.withAudience
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
    command("unregister") {
        stringArgument(
            alias = "username",
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
                        }.getOrNull() ?: return@launch

                    authDao.deleteAccount(authData.uuid)
                        .onSuccess {
                            ctx.source.server
                                .playerList
                                .getPlayerByName(usernameToDelete)
                                ?.let { serverPlayer ->
                                    val playerModel = PlayerLoginModel(
                                        username = serverPlayer.name.toPlain(),
                                        uuid = serverPlayer.uuid,
                                        ip = serverPlayer.ipAddress
                                    )
                                    authorizedApi.forgetUser(playerModel.uuid)
                                    authorizedApi.loadUserInfo(playerModel)
                                }
                            kyoriKrate
                                .withAudience(ctx.source)
                                .sendSystemMessage(translation.userDeleted)
                        }.onFailure {
                            kyoriKrate
                                .withAudience(ctx.source)
                                .sendSystemMessage(translation.userCouldNotBeDeleted)
                        }
                }
            }
        )
    }
}
