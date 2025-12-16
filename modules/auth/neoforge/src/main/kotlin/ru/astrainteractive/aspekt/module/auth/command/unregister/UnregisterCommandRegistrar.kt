package ru.astrainteractive.aspekt.module.auth.command.unregister

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.minecraft.commands.CommandSourceStack
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.model.PlayerLoginModel
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthPermission
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.astralibs.command.util.argument
import ru.astrainteractive.astralibs.command.util.command
import ru.astrainteractive.astralibs.command.util.hints
import ru.astrainteractive.astralibs.command.util.requireArgument
import ru.astrainteractive.astralibs.command.util.requirePermission
import ru.astrainteractive.astralibs.command.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.util.NeoForgeUtil
import ru.astrainteractive.astralibs.server.util.asAudience
import ru.astrainteractive.astralibs.server.util.getOnlinePlayer
import ru.astrainteractive.astralibs.server.util.getOnlinePlayers
import ru.astrainteractive.astralibs.server.util.getPlayerGameProfile
import ru.astrainteractive.astralibs.server.util.toPlain
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

/**
 * /unregister <username>
 */
class UnregisterCommandRegistrar(
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<AuthTranslation>,
    private val ioScope: CoroutineScope,
    private val authDao: AuthDao,
    private val authorizedApi: AuthorizedApi,
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate

    fun createNode(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("unregister") {
            argument(
                alias = "username",
                type = StringArgumentType.string(),
                block = { usernameArg ->
                    hints { NeoForgeUtil.getOnlinePlayers().map { player -> player.name.toPlain() } }
                    runs { ctx ->
                        ctx.requirePermission(AuthPermission.Unregister)
                        val usernameToDelete = ctx.requireArgument(usernameArg)
                        ioScope.launch {
                            val authData = authDao.getUser(usernameToDelete)
                                .onFailure {
                                    ctx.source
                                        .asAudience()
                                        .sendMessage(translation.userNotFound.component)
                                }.getOrNull() ?: return@launch

                            authDao.deleteAccount(authData.uuid)
                                .onSuccess {
                                    val onlinePlayerLoginModel = NeoForgeUtil
                                        .getOnlinePlayer(usernameToDelete)
                                        ?.let { player ->
                                            PlayerLoginModel(
                                                username = player.name.toPlain(),
                                                uuid = player.uuid,
                                                ip = player.ipAddress
                                            )
                                        }
                                    val offlinePlayerLoginModel = NeoForgeUtil
                                        .getPlayerGameProfile(usernameToDelete)
                                        ?.let { profile ->
                                            PlayerLoginModel(
                                                username = profile.name,
                                                uuid = profile.id,
                                                ip = null.orEmpty()
                                            )
                                        }
                                    val playerLoginModel = onlinePlayerLoginModel ?: offlinePlayerLoginModel
                                    playerLoginModel
                                        ?.uuid
                                        ?.let { playerLoginModel ->
                                            authorizedApi.forgetUser(playerLoginModel)
                                            ctx.source
                                                .asAudience()
                                                .sendMessage(translation.userDeleted.component)
                                        }
                                    onlinePlayerLoginModel
                                        ?.let(authorizedApi::loadUserInfo)
                                }.onFailure {
                                    ctx.source
                                        .asAudience()
                                        .sendMessage(translation.userCouldNotBeDeleted.component)
                                }
                        }
                    }
                }
            )
        }
    }
}
