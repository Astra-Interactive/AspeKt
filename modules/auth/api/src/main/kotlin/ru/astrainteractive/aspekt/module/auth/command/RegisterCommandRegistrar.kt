package ru.astrainteractive.aspekt.module.auth.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.core.command.CommandExceptionHandler
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.dao.api.AuthRepository
import ru.astrainteractive.aspekt.module.auth.dao.model.AuthData
import ru.astrainteractive.aspekt.module.auth.dao.util.isRegistered
import ru.astrainteractive.aspekt.module.auth.model.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.util.sha256
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

/**
 * /register <password> <password_confirm>
 */
class RegisterCommandRegistrar(
    translationKrate: CachedKrate<AuthTranslation>,
    private val ioScope: CoroutineScope,
    private val authRepository: AuthRepository,
    private val authorizedApi: AuthorizedApi,
    private val multiplatformCommand: MultiplatformCommand,
    private val commandExceptionHandler: CommandExceptionHandler
) {
    private val translation by translationKrate

    private fun createNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("register") {
                argument(
                    alias = "password",
                    type = StringArgumentType.string(),
                    block = { passwordArg ->
                        argument(
                            alias = "password_confirm",
                            type = StringArgumentType.string(),
                            block = {
                                runs(commandExceptionHandler::handle) { ctx ->
                                    ioScope.launch {
                                        val player = ctx.requirePlayer()
                                        val passwordSha = ctx.requireArgument(passwordArg).sha256()
                                        val isRegistered = authRepository.isRegistered(player.uuid)
                                        if (isRegistered) {
                                            ctx.getSender().sendMessage(translation.alreadyRegistered)
                                            return@launch
                                        }
                                        val authData = AuthData(
                                            lastUsername = player.name,
                                            uuid = player.uuid,
                                            passwordSha256 = passwordSha,
                                            lastIpAddress = player.address.hostName
                                        )
                                        authRepository.createAccount(authData)
                                            .onFailure {
                                                ctx.getSender().sendMessage(translation.couldNotCreateAccount)
                                            }
                                            .onSuccess {
                                                ctx.getSender().sendMessage(translation.accountCreated)
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

    fun createNodes(): List<LiteralArgumentBuilder<Any>> {
        return listOf(createNode())
    }
}
