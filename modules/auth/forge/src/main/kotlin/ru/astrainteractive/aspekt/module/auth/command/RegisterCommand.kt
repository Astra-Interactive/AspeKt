package ru.astrainteractive.aspekt.module.auth.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.argument
import ru.astrainteractive.aspekt.core.forge.command.util.command
import ru.astrainteractive.aspekt.core.forge.command.util.requireArgument
import ru.astrainteractive.aspekt.core.forge.command.util.runs
import ru.astrainteractive.aspekt.core.forge.util.asAudience
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.isRegistered
import ru.astrainteractive.aspekt.module.auth.api.model.AuthData
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.api.util.sha256
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

@Suppress("LongMethod")
fun RegisterCommandsEvent.registerCommand(
    scope: CoroutineScope,
    authDao: AuthDao,
    authorizedApi: AuthorizedApi,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<AuthTranslation>,
) {
    command("register") {
        argument(alias = "password", com.mojang.brigadier.arguments.StringArgumentType.string()) {
            argument("password_confirm", com.mojang.brigadier.arguments.StringArgumentType.string()) {
                runs { ctx ->
                    scope.launch {
                        with(kyoriKrate.cachedValue) {
                            val translation = translationKrate.cachedValue
                            val player = ctx.source.entity as? ServerPlayer
                            player ?: run {
                                ctx.source
                                    .asAudience()
                                    .sendMessage(translation.onlyPlayerCommand.component)
                                return@launch
                            }
                            val passwordSha = ctx.requireArgument("password", StringArgumentType).sha256()
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
                                }.onSuccess {
                                    ctx.source
                                        .asAudience()
                                        .sendMessage(translation.accountCreated.component)
                                    authorizedApi.authUser(player.uuid)
                                }
                        }
                    }
                }
            }
        }
    }.run(dispatcher::register)
}
