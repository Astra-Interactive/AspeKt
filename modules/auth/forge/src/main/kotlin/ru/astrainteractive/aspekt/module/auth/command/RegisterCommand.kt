package ru.astrainteractive.aspekt.module.auth.command

import com.mojang.brigadier.arguments.StringArgumentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.argument
import ru.astrainteractive.aspekt.core.forge.command.util.command
import ru.astrainteractive.aspekt.core.forge.util.sha256
import ru.astrainteractive.aspekt.core.forge.util.toNative
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
        argument(
            alias = "password",
            type = StringArgumentType.string(),
            builder = {
                argument(
                    alias = "password_confirm",
                    type = StringArgumentType.string(),
                    execute = execute@{ ctx ->
                        val player = ctx.source.entity as? ServerPlayer
                        player ?: run {
                            with(kyoriKrate.cachedValue) {
                                StringDesc.Raw("Команда только для игроков!")
                                    .component
                                    .toNative()
                                    .run(ctx.source::sendSystemMessage)
                            }
                            return@execute
                        }
                        val passwordSha = ctx.getArgument(
                            "password",
                            String::class.java
                        ).sha256()
                        scope.launch {
                            val isRegistered = authDao.isRegistered(player.uuid)
                            if (isRegistered) {
                                with(kyoriKrate.cachedValue) {
                                    StringDesc.Raw("Вы уже зарегистрированы!")
                                        .component
                                        .toNative()
                                        .run(ctx.source::sendSystemMessage)
                                }
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
                                    with(kyoriKrate.cachedValue) {
                                        StringDesc.Raw("Не удалось создать аккаунт!")
                                            .component
                                            .toNative()
                                            .run(ctx.source::sendSystemMessage)
                                    }
                                }.onSuccess {
                                    with(kyoriKrate.cachedValue) {
                                        StringDesc.Raw("Аккаунт создан успешно!")
                                            .component
                                            .toNative()
                                            .run(ctx.source::sendSystemMessage)
                                    }
                                    authorizedApi.authUser(player.uuid)
                                }
                        }
                    }
                )
            }
        )
    }
}
