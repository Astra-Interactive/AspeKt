package ru.astrainteractive.aspekt.module.auth.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.command
import ru.astrainteractive.aspekt.core.forge.command.util.requireArgument
import ru.astrainteractive.aspekt.core.forge.command.util.stringArgument
import ru.astrainteractive.aspekt.core.forge.kyori.sendSystemMessage
import ru.astrainteractive.aspekt.core.forge.kyori.withAudience
import ru.astrainteractive.aspekt.core.forge.util.getValue
import ru.astrainteractive.aspekt.core.forge.util.sha256
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.isRegistered
import ru.astrainteractive.aspekt.module.auth.api.model.AuthData
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.util.CacheOwnerExt.getValue
import ru.astrainteractive.klibs.kstorage.util.getValue

@Suppress("LongMethod")
fun RegisterCommandsEvent.registerCommand(
    scope: CoroutineScope,
    authDao: AuthDao,
    authorizedApi: AuthorizedApi,
    kyoriKrate: Krate<KyoriComponentSerializer>,
    translationKrate: Krate<AuthTranslation>
) {
    command("register") {
        stringArgument(
            alias = "password",
            builder = {
                stringArgument(
                    alias = "password_confirm",
                    execute = execute@{ ctx ->
                        val translation = translationKrate.cachedValue
                        val player = ctx.source.entity as? ServerPlayer
                        player ?: run {
                            kyoriKrate
                                .withAudience(ctx.source)
                                .sendSystemMessage(translation.onlyPlayerCommand)
                            return@execute
                        }
                        val passwordSha = ctx.requireArgument("password", StringArgumentType).sha256()
                        scope.launch {
                            val isRegistered = authDao.isRegistered(player.uuid)
                            if (isRegistered) {
                                kyoriKrate
                                    .withAudience(ctx.source)
                                    .sendSystemMessage(translation.alreadyRegistered)
                                return@launch
                            }
                            val authData = AuthData(
                                lastUsername = player.name.toPlain(),
                                uuid = player.uuid,
                                passwordSha256 = passwordSha,
                                lastIpAddress = player.ipAddress
                            )
                            authDao.createAccount(authData)
                                .onFailure {
                                    kyoriKrate
                                        .withAudience(ctx.source)
                                        .sendSystemMessage(translation.couldNotCreateAccount)
                                }.onSuccess {
                                    kyoriKrate
                                        .withAudience(ctx.source)
                                        .sendSystemMessage(translation.accountCreated)
                                    authorizedApi.authUser(player.uuid)
                                }
                        }
                    }
                )
            }
        )
    }
}
