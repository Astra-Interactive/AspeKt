package ru.astrainteractive.aspekt.module.auth.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.command
import ru.astrainteractive.aspekt.core.forge.command.util.stringArgument
import ru.astrainteractive.aspekt.core.forge.kyori.sendSystemMessage
import ru.astrainteractive.aspekt.core.forge.kyori.withAudience
import ru.astrainteractive.aspekt.core.forge.util.sha256
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.isRegistered
import ru.astrainteractive.aspekt.module.auth.api.model.AuthData
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.Krate

@Suppress("LongMethod")
fun RegisterCommandsEvent.registerCommand(
    scope: CoroutineScope,
    authDao: AuthDao,
    authorizedApi: AuthorizedApi,
    kyoriKrate: Krate<KyoriComponentSerializer>
) {
    command("register") {
        stringArgument(
            alias = "password",
            builder = {
                stringArgument(
                    alias = "password_confirm",
                    execute = execute@{ ctx ->
                        val player = ctx.source.entity as? ServerPlayer
                        player ?: run {
                            kyoriKrate
                                .withAudience(ctx.source)
                                .sendSystemMessage(StringDesc.Raw("Команда только для игроков!"))
                            return@execute
                        }
                        val passwordSha = ctx.getArgument(
                            "password",
                            String::class.java
                        ).sha256()
                        scope.launch {
                            val isRegistered = authDao.isRegistered(player.uuid)
                            if (isRegistered) {
                                kyoriKrate
                                    .withAudience(ctx.source)
                                    .sendSystemMessage(StringDesc.Raw("Вы уже зарегистрированы!"))
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
                                        .sendSystemMessage(StringDesc.Raw("Не удалось создать аккаунт!"))
                                }.onSuccess {
                                    kyoriKrate
                                        .withAudience(ctx.source)
                                        .sendSystemMessage(StringDesc.Raw("Аккаунт создан успешно!"))
                                    authorizedApi.authUser(player.uuid)
                                }
                        }
                    }
                )
            }
        )
    }
}
