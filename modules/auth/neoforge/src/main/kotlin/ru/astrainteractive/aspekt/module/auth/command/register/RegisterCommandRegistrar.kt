package ru.astrainteractive.aspekt.module.auth.command.register

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minecraft.commands.CommandSourceStack
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.isRegistered
import ru.astrainteractive.aspekt.module.auth.api.model.AuthData
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.api.util.sha256
import ru.astrainteractive.astralibs.command.util.argument
import ru.astrainteractive.astralibs.command.util.command
import ru.astrainteractive.astralibs.command.util.requireArgument
import ru.astrainteractive.astralibs.command.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.util.asAudience
import ru.astrainteractive.astralibs.server.util.toPlain
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

/**
 * /register <password> <password_confirm>
 */
class RegisterCommandRegistrar(
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<AuthTranslation>,
    private val ioScope: CoroutineScope,
    private val authDao: AuthDao,
    private val authorizedApi: AuthorizedApi,
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate

    fun createNode(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("register") {
            argument(
                alias = "password",
                type = StringArgumentType.string(),
                block = { passwordArg ->
                    argument(
                        alias = "password_confirm",
                        type = StringArgumentType.string(),
                        block = {
                            runs { ctx ->
                                ioScope.launch {
                                    val player = ctx.source.player
                                    player ?: run {
                                        ctx.source
                                            .asAudience()
                                            .sendMessage(translation.onlyPlayerCommand.component)
                                        return@launch
                                    }
                                    val passwordSha = ctx.requireArgument(passwordArg).sha256()
                                    val isRegistered = authDao.isRegistered(player.uuid)
                                    if (isRegistered) {
                                        ctx.source
                                            .asAudience()
                                            .sendMessage(translation.alreadyRegistered.component)
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
                                            ctx.source
                                                .asAudience()
                                                .sendMessage(translation.couldNotCreateAccount.component)
                                        }
                                        .onSuccess {
                                            ctx.source
                                                .asAudience()
                                                .sendMessage(translation.accountCreated.component)
                                            authorizedApi.authUser(player.uuid)
                                        }
                                }
                            }
                        }
                    )
                }
            )
        }
    }
}
