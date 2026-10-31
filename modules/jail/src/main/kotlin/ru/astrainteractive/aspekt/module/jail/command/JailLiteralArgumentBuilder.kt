package ru.astrainteractive.aspekt.module.jail.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.aspekt.module.jail.api.JailCache
import ru.astrainteractive.aspekt.module.jail.api.JailRepository
import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.aspekt.module.jail.usecase.FreeInmateUseCase
import ru.astrainteractive.aspekt.module.jail.usecase.TeleportInmateToJailUseCase
import ru.astrainteractive.aspekt.module.jail.util.sendMessage
import ru.astrainteractive.aspekt.module.jail.util.toJailLocation
import ru.astrainteractive.aspekt.plugin.model.PluginPermission
import ru.astrainteractive.aspekt.plugin.model.PluginTranslation
import ru.astrainteractive.aspekt.plugin.util.created
import ru.astrainteractive.aspekt.plugin.util.deleted
import ru.astrainteractive.aspekt.plugin.util.hasInmates
import ru.astrainteractive.aspekt.plugin.util.inmateJailed
import ru.astrainteractive.aspekt.plugin.util.inmateReleased
import ru.astrainteractive.aspekt.plugin.util.jailed
import ru.astrainteractive.aspekt.plugin.util.list
import ru.astrainteractive.astralibs.command.api.argumenttype.OfflinePlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import kotlin.time.Clock

@Suppress("LongParameterList")
internal class JailLiteralArgumentBuilder(
    translationKrate: CachedKrate<PluginTranslation>,
    private val scope: CoroutineScope,
    private val jailRepository: JailRepository,
    private val jailCache: JailCache,
    private val freeInmateUseCase: FreeInmateUseCase,
    private val teleportInmateToJailUseCase: TeleportInmateToJailUseCase,
    private val multiplatformCommand: MultiplatformCommand,
    private val platformServer: PlatformServer,
    private val clock: Clock,
    private val commandExceptionHandler: CommandExceptionHandler
) {
    private val translation by translationKrate

    @Suppress("LongMethod")
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("jail") {
                literal("list") {
                    runs(commandExceptionHandler::onException) { ctx ->
                        ctx.requirePermission(PluginPermission.JAIL_LIST)
                        scope.launch {
                            val jails = jailRepository.findJails().getOrNull().orEmpty().map(Jail::name)
                            val jailsString = jails.joinToString()
                            ctx.getSender().sendMessage(translation.jails.list(jailsString))
                        }
                    }
                }
                literal("create") {
                    argument(
                        "jail",
                        StringArgumentType.string()
                    ) { jailArg ->
                        runs(commandExceptionHandler::onException) { ctx ->
                            ctx.requirePermission(PluginPermission.JAIL_CREATE)
                            val player = ctx.requirePlayer()
                            val jail = Jail(
                                name = ctx.requireArgument(jailArg),
                                location = player.getLocation().toJailLocation()
                            )
                            scope.launch {
                                jailRepository.addJail(jail)
                                    .onFailure {
                                        ctx.getSender().sendMessage(translation.jails.creationFailed)
                                    }
                                    .onSuccess {
                                        ctx.getSender().sendMessage(
                                            translation.jails.created(jail.name)
                                        )
                                    }
                            }
                        }
                    }
                }
                literal("delete") {
                    argument(
                        "jail",
                        StringArgumentType.string()
                    ) { jailArg ->
                        hints { jailCache.findJails().map(Jail::name) }
                        runs(commandExceptionHandler::onException) { ctx ->
                            ctx.requirePermission(PluginPermission.JAIL_DELETE)
                            scope.launch {
                                val jailName = ctx.requireArgument(jailArg)
                                if (jailRepository.findJailInmates(jailName).getOrNull().orEmpty().isNotEmpty()) {
                                    ctx.getSender().sendMessage(translation.jails.hasInmates(jailName))
                                } else {
                                    jailRepository.deleteJail(jailName)
                                        .onFailure {
                                            ctx.getSender().sendMessage(translation.jails.deletionFailed)
                                        }
                                        .onSuccess {
                                            ctx.getSender().sendMessage(
                                                translation.jails.deleted(jailName)
                                            )
                                        }
                                }
                            }
                        }
                    }
                }
                literal("free") {
                    argument(
                        "player",
                        StringArgumentType.string()
                    ) { playerArg ->
                        hints { platformServer.getOnlinePlayers().map(OnlineKPlayer::name) }
                        runs(commandExceptionHandler::onException) { ctx ->
                            ctx.requirePermission(PluginPermission.JAIL_FREE)
                            scope.launch {
                                val offlinePlayerToFree = ctx.requireArgument(playerArg, OfflinePlayerArgumentConverter)
                                val inmate = jailRepository.findInmate(offlinePlayerToFree.uniqueId.toString())
                                    .getOrNull()
                                    ?: return@launch

                                freeInmateUseCase.invoke(inmate)
                                    .onFailure {
                                        ctx.getSender().sendMessage(translation.jails.releaseFailed)
                                    }
                                    .onSuccess {
                                        jailCache.cache(inmate.uuid)

                                        offlinePlayerToFree.sendMessage(translation.jails.released)
                                        ctx.getSender().sendMessage(
                                            translation.jails.inmateReleased(
                                                offlinePlayerToFree.name.orEmpty()
                                            )
                                        )
                                    }
                            }
                        }
                    }
                }
                literal("inmate") {
                    argument(
                        "jail",
                        StringArgumentType.string()
                    ) { jailArg ->
                        hints { jailCache.findJails().map(Jail::name) }
                        argument(
                            "player",
                            StringArgumentType.string()
                        ) { playerArg ->
                            hints { platformServer.getOnlinePlayers().map(OnlineKPlayer::name) }
                            argument(
                                "time",
                                StringArgumentType.string()
                            ) { timeArg ->
                                hints { listOf("TIME:1s,1m,1h10m") }
                                runs(commandExceptionHandler::onException) { ctx ->
                                    ctx.requirePermission(PluginPermission.JAIL_INMATE)
                                    scope.launch {
                                        val jailName = ctx.requireArgument(jailArg)
                                        val jailOfflinePlayer = ctx.requireArgument(
                                            playerArg,
                                            OfflinePlayerArgumentConverter
                                        )
                                        val jailDuration = ctx.requireArgument(timeArg, DurationArgumentType)

                                        val inmate = JailInmate(
                                            uuid = jailOfflinePlayer.uniqueId.toString(),
                                            jailName = jailName,
                                            start = clock.now(),
                                            duration = jailDuration,
                                            lastUsername = jailOfflinePlayer.name.orEmpty(),
                                            lastLocation = jailOfflinePlayer.location
                                                ?.toJailLocation()
                                                ?: Bukkit.getWorlds().first().spawnLocation.toJailLocation()
                                        )
                                        jailRepository.addInmate(inmate)
                                            .onFailure {
                                                ctx.getSender().sendMessage(translation.jails.jailingFailed)
                                            }
                                            .onSuccess {
                                                jailOfflinePlayer.sendMessage(
                                                    translation.jails.jailed(jailDuration)
                                                )
                                                ctx.getSender().sendMessage(
                                                    translation.jails.inmateJailed(
                                                        name = jailOfflinePlayer.name.orEmpty(),
                                                        jail = jailName
                                                    )
                                                )
                                                jailCache.cache(inmate.uuid)
                                                teleportInmateToJailUseCase.invoke(inmate)
                                            }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
