package ru.astrainteractive.aspekt.module.auth.command.login

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.checkAuthDataIsValid
import ru.astrainteractive.aspekt.module.auth.api.isRegistered
import ru.astrainteractive.aspekt.module.auth.api.model.AuthData
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.api.util.sha256
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.KAudience
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.util.tryCast

/**
 * /login <password>
 */
class LoginLiteralArgumentBuilder(
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<AuthTranslation>,
    private val ioScope: CoroutineScope,
    private val authDao: AuthDao,
    private val authorizedApi: AuthorizedApi,
    private val multiplatformCommand: MultiplatformCommand
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("login") {
                argument(
                    alias = "password",
                    type = StringArgumentType.string(),
                    block = { passwordArg ->
                        runs { ctx ->
                            val player = ctx.requirePlayer()
                            ioScope.launch {
                                val passwordSha = ctx.requireArgument(passwordArg).sha256()
                                val isRegistered = authDao.isRegistered(player.uuid)
                                if (!isRegistered) {
                                    ctx.getSender()
                                        .tryCast<KAudience>()
                                        ?.sendMessage(translation.notRegistered.component)
                                    return@launch
                                }
                                val authData = AuthData(
                                    lastUsername = player.name,
                                    uuid = player.uuid,
                                    passwordSha256 = passwordSha,
                                    lastIpAddress = player.address.hostName
                                )
                                if (authDao.checkAuthDataIsValid(authData).getOrDefault(false)) {
                                    ctx.getSender()
                                        .tryCast<KAudience>()
                                        ?.sendMessage(translation.authSuccess.component)
                                    authorizedApi.authUser(player.uuid)
                                } else {
                                    ctx.getSender()
                                        .tryCast<KAudience>()
                                        ?.sendMessage(translation.wrongPassword.component)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}
