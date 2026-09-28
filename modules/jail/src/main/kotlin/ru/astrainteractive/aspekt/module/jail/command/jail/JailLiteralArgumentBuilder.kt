package ru.astrainteractive.aspekt.module.jail.command.jail

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.module.jail.command.argumenttype.DurationArgumentType
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApi
import ru.astrainteractive.aspekt.module.jail.data.JailApi
import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.aspekt.module.jail.util.sendMessage
import ru.astrainteractive.aspekt.module.jail.util.toJailLocation
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.argumenttype.OfflinePlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import java.time.Instant

/**
 * /jail list
 * /jail create <jail>
 * /jail delete <jail>
 * /jail free <player>
 * /jail inmate <jail> <player> <time>
 */
@Suppress("LongParameterList")
internal class JailLiteralArgumentBuilder(
    translationKrate: CachedKrate<PluginTranslation>,
    private val scope: CoroutineScope,
    private val jailApi: JailApi,
    private val cachedJailApi: CachedJailApi,
    private val jailController: ru.astrainteractive.aspekt.module.jail.controller.JailController,
    private val multiplatformCommand: MultiplatformCommand,
    private val platformServer: PlatformServer
) {
    private val translation by translationKrate

    @Suppress("LongMethod")
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("jail") {
                literal("list") {
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.JAIL_LIST)
                        scope.launch {
                            val jails = jailApi.getJails().getOrNull().orEmpty().map(Jail::name)
                            val jailsString = jails.joinToString()
                            ctx.getSender().sendMessage(translation.jails.list(jailsString))
                        }
                    }
                }
                literal("create") {
                    argument("jail", StringArgumentType.string()) { jailArg ->
                        runs { ctx ->
                            ctx.requirePermission(PluginPermission.JAIL_CREATE)
                            val player = ctx.requirePlayer()
                            val jail = Jail(
                                name = ctx.requireArgument(jailArg),
                                location = player.getLocation().toJailLocation()
                            )
                            scope.launch {
                                jailApi.addJail(jail)
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
                    argument("jail", StringArgumentType.string()) { jailArg ->
                        hints { cachedJailApi.getJails().map(Jail::name) }
                        runs { ctx ->
                            ctx.requirePermission(PluginPermission.JAIL_DELETE)
                            scope.launch {
                                val jailName = ctx.requireArgument(jailArg)
                                if (jailApi.getJailInmates(jailName).getOrNull().orEmpty().isNotEmpty()) {
                                    ctx.getSender().sendMessage(translation.jails.hasInmates(jailName))
                                } else {
                                    jailApi.deleteJail(jailName)
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
                    argument("player", StringArgumentType.string()) { playerArg ->
                        hints { platformServer.getOnlinePlayers().map(OnlineKPlayer::name) }
                        runs { ctx ->
                            ctx.requirePermission(PluginPermission.JAIL_FREE)
                            scope.launch {
                                val offlinePlayerToFree = ctx.requireArgument(playerArg, OfflinePlayerArgumentConverter)
                                val inmate = jailApi.getInmate(offlinePlayerToFree.uniqueId.toString())
                                    .getOrNull()
                                    ?: return@launch

                                jailApi.free(offlinePlayerToFree.uniqueId.toString())
                                    .onFailure {
                                        ctx.getSender().sendMessage(translation.jails.releaseFailed)
                                    }
                                    .onSuccess {
                                        jailController.free(inmate)
                                        cachedJailApi.cache(inmate.uuid)

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
                    argument("jail", StringArgumentType.string()) { jailArg ->
                        hints { cachedJailApi.getJails().map(Jail::name) }
                        argument("player", StringArgumentType.string()) { playerArg ->
                            hints { platformServer.getOnlinePlayers().map(OnlineKPlayer::name) }
                            argument("time", StringArgumentType.string()) { timeArg ->
                                hints { listOf("TIME:1s,1m,1h10m") }
                                runs { ctx ->
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
                                            start = Instant.now(),
                                            duration = jailDuration,
                                            lastUsername = jailOfflinePlayer.name.orEmpty(),
                                            lastLocation = jailOfflinePlayer.location
                                                ?.toJailLocation()
                                                ?: Bukkit.getWorlds().first().spawnLocation.toJailLocation()
                                        )
                                        jailApi.addInmate(inmate)
                                            .onFailure {
                                                ctx.getSender().sendMessage(translation.jails.jailingFailed)
                                            }
                                            .onSuccess {
                                                jailOfflinePlayer.sendMessage(
                                                    translation.jails.jailed(
                                                        jailDuration.toString()
                                                    )
                                                )
                                                ctx.getSender().sendMessage(
                                                    translation.jails.inmateJailed(
                                                        name = jailOfflinePlayer.name.orEmpty(),
                                                        jail = jailName
                                                    )
                                                )
                                                cachedJailApi.cache(inmate.uuid)
                                                jailController.onJailed(inmate)
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
