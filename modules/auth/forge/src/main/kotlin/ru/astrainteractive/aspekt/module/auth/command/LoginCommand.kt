package ru.astrainteractive.aspekt.module.auth.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.checkAuthDataIsValid
import ru.astrainteractive.aspekt.module.auth.api.isRegistered
import ru.astrainteractive.aspekt.module.auth.api.model.AuthData
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.api.util.sha256
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.command.util.argument
import ru.astrainteractive.astralibs.command.util.command
import ru.astrainteractive.astralibs.command.util.requireArgument
import ru.astrainteractive.astralibs.command.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.util.asAudience
import ru.astrainteractive.astralibs.server.util.toPlain
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

fun RegisterCommandsEvent.loginCommand(
    scope: CoroutineScope,
    authDao: AuthDao,
    authorizedApi: AuthorizedApi,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<AuthTranslation>
) {
    with(kyoriKrate.unwrap()) {
        command("login") {
            argument(alias = "password", com.mojang.brigadier.arguments.StringArgumentType.string()) {
                runs { ctx ->
                    val translation = translationKrate.cachedValue
                    val player = ctx.source.entity as? ServerPlayer
                    scope.launch {
                        player ?: run {
                            ctx.source
                                .asAudience()
                                .sendMessage(translation.onlyPlayerCommand.component)
                            return@launch
                        }

                        val passwordSha = ctx.requireArgument("password", StringArgumentType).sha256()
                        val isRegistered = authDao.isRegistered(player.uuid)
                        if (!isRegistered) {
                            ctx.source
                                .asAudience()
                                .sendMessage(translation.notRegistered.component)
                            return@launch
                        }
                        val authData = AuthData(
                            lastUsername = player.name.toPlain(),
                            uuid = player.uuid,
                            passwordSha256 = passwordSha,
                            lastIpAddress = player.ipAddress
                        )
                        if (authDao.checkAuthDataIsValid(authData).getOrDefault(false)) {
                            ctx.source
                                .asAudience()
                                .sendMessage(translation.authSuccess.component)
                            authorizedApi.authUser(player.uuid)
                        } else {
                            ctx.source
                                .asAudience()
                                .sendMessage(translation.wrongPassword.component)
                        }
                    }
                }
            }
        }.run(dispatcher::register)
    }
}
