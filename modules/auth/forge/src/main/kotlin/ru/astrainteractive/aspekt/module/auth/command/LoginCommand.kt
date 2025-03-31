package ru.astrainteractive.aspekt.module.auth.command

import com.mojang.brigadier.arguments.StringArgumentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minecraft.world.entity.player.Player
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.argument
import ru.astrainteractive.aspekt.core.forge.command.util.command
import ru.astrainteractive.aspekt.core.forge.util.sha256
import ru.astrainteractive.aspekt.core.forge.util.toKyori
import ru.astrainteractive.aspekt.core.forge.util.toNative
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
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
        argument(
            alias = "password",
            type = StringArgumentType.string(),
            execute = execute@{ ctx ->
                val player = ctx.source.entity as? Player
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
                    val isRegistered = authDao.isRegistered(player.uuid).getOrDefault(false)
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
                        lastUsername = player.name.toKyori().toPlain(),
                        uuid = player.uuid,
                        passwordSha256 = passwordSha
                    )
                    if (authDao.checkAuthDataIsOk(authData).getOrDefault(false)) {
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
