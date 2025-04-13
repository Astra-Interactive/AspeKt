package ru.astrainteractive.aspekt.module.auth.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.literal
import ru.astrainteractive.aspekt.core.forge.command.util.requireArgument
import ru.astrainteractive.aspekt.core.forge.command.util.stringArgument
import ru.astrainteractive.aspekt.core.forge.kyori.sendSystemMessage
import ru.astrainteractive.aspekt.core.forge.kyori.withAudience
import ru.astrainteractive.aspekt.core.forge.util.sha256
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.checkAuthDataIsValid
import ru.astrainteractive.aspekt.module.auth.api.isRegistered
import ru.astrainteractive.aspekt.module.auth.api.model.AuthData
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.Krate

fun RegisterCommandsEvent.loginCommand(
    scope: CoroutineScope,
    authDao: AuthDao,
    authorizedApi: AuthorizedApi,
    kyoriKrate: Krate<KyoriComponentSerializer>,
    translationKrate: Krate<AuthTranslation>
) {
    literal("login") {
        stringArgument(
            alias = "password",
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
                    if (!isRegistered) {
                        kyoriKrate
                            .withAudience(ctx.source)
                            .sendSystemMessage(translation.notRegistered)
                        return@launch
                    }
                    val authData = AuthData(
                        lastUsername = player.name.toPlain(),
                        uuid = player.uuid,
                        passwordSha256 = passwordSha,
                        lastIpAddress = player.ipAddress
                    )
                    if (authDao.checkAuthDataIsValid(authData).getOrDefault(false)) {
                        kyoriKrate
                            .withAudience(ctx.source)
                            .sendSystemMessage(translation.authSuccess)
                        authorizedApi.authUser(player.uuid)
                    } else {
                        kyoriKrate
                            .withAudience(ctx.source)
                            .sendSystemMessage(translation.wrongPassword)
                    }
                }
            }
        )
    }
}
