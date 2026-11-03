package ru.astrainteractive.aspekt.module.auth.api.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.aspekt.module.auth.api.api.AuthStateRepository
import ru.astrainteractive.aspekt.module.auth.api.model.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.api.util.sha256
import ru.astrainteractive.aspekt.module.auth.dao.api.AuthRepository
import ru.astrainteractive.aspekt.module.auth.dao.model.AuthData
import ru.astrainteractive.aspekt.module.auth.dao.util.checkAuthDataIsValid
import ru.astrainteractive.aspekt.module.auth.dao.util.isRegistered
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

internal class LoginCommandRegistrar(
    translationKrate: CachedKrate<AuthTranslation>,
    private val ioScope: CoroutineScope,
    private val authRepository: AuthRepository,
    private val authStateRepository: AuthStateRepository,
    private val multiplatformCommand: MultiplatformCommand,
    private val commandExceptionHandler: CommandExceptionHandler
) {
    private val translation by translationKrate

    private fun createNode(loginAlias: String): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command(loginAlias) {
                argument(
                    alias = "password",
                    type = StringArgumentType.string(),
                    block = { passwordArg ->
                        runs(commandExceptionHandler::onException) { ctx ->
                            val player = ctx.requirePlayer()
                            ioScope.launch {
                                val passwordSha = ctx.requireArgument(passwordArg).sha256()
                                val isRegistered = authRepository.isRegistered(player.uuid)
                                if (!isRegistered) {
                                    ctx.getSender().sendMessage(translation.notRegistered)
                                    return@launch
                                }
                                val authData = AuthData(
                                    lastUsername = player.name,
                                    uuid = player.uuid,
                                    passwordSha256 = passwordSha,
                                    lastIpAddress = player.address.hostName
                                )
                                if (authRepository.checkAuthDataIsValid(authData).getOrDefault(false)) {
                                    ctx.getSender().sendMessage(translation.authSuccess)
                                    authStateRepository.authUser(player.uuid)
                                } else {
                                    ctx.getSender().sendMessage(translation.wrongPassword)
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    fun createNodes(): List<LiteralArgumentBuilder<Any>> {
        return listOf(createNode("login"), createNode("l"))
    }
}
