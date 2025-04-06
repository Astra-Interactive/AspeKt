package ru.astrainteractive.aspekt.module.auth.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.command
import ru.astrainteractive.aspekt.core.forge.command.util.stringArgument
import ru.astrainteractive.aspekt.core.forge.util.sha256
import ru.astrainteractive.aspekt.core.forge.util.toNative
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.checkAuthDataIsValid
import ru.astrainteractive.aspekt.module.auth.api.isRegistered
import ru.astrainteractive.aspekt.module.auth.api.model.AuthData
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.Krate

fun RegisterCommandsEvent.loginCommand(
    scope: CoroutineScope,
    authDao: AuthDao,
    authorizedApi: AuthorizedApi,
    kyoriKrate: Krate<KyoriComponentSerializer>
) {
    command("login") {
        stringArgument(
            alias = "password",
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
                    if (!isRegistered) {
                        with(kyoriKrate.cachedValue) {
                            StringDesc.Raw("Вы не зарегистрированы! /login ПАРОЛЬ ПАРОЛЬ")
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
                    if (authDao.checkAuthDataIsValid(authData).getOrDefault(false)) {
                        with(kyoriKrate.cachedValue) {
                            StringDesc.Raw("Вы успешно авторизованы!")
                                .component
                                .toNative()
                                .run(ctx.source::sendSystemMessage)
                        }
                        authorizedApi.authUser(player.uuid)
                    } else {
                        with(kyoriKrate.cachedValue) {
                            StringDesc.Raw("Пароль неверный!")
                                .component
                                .toNative()
                                .run(ctx.source::sendSystemMessage)
                        }
                    }
                }
            }
        )
    }
}
