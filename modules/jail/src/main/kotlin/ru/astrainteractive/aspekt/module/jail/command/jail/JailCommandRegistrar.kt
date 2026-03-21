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
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.KAudience
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.util.tryCast
import java.time.Instant

/**
 * /jail list
 * /jail create <jail>
 * /jail delete <jail>
 * /jail free <player>
 * /jail inmate <jail> <player> <time>
 */
@Suppress("LongParameterList")
internal class JailCommandRegistrar(
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val scope: CoroutineScope,
    private val jailApi: JailApi,
    private val cachedJailApi: CachedJailApi,
    private val jailController: ru.astrainteractive.aspekt.module.jail.controller.JailController,
    private val multiplatformCommand: MultiplatformCommand,
    private val platformServer: PlatformServer
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate

    @Suppress("LongMethod")
    fun createNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("jail") {
                literal("list") {
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.JAIL_LIST)
                        scope.launch {
                            val jails = jailApi.getJails().getOrNull().orEmpty().map(Jail::name)
                            val jailsString = jails.joinToString()
                            ctx.getSender().tryCast<KAudience>()
                                ?.sendMessage(translation.jails.jailsList(jailsString).component)
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
                                        ctx.getSender().tryCast<KAudience>()
                                            ?.sendMessage(translation.jails.jailCreatedFail.component)
                                    }
                                    .onSuccess {
                                        ctx.getSender().tryCast<KAudience>()
                                            ?.sendMessage(translation.jails.jailCreatedSuccess(jail.name).component)
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
                                    ctx.getSender().tryCast<KAudience>()
                                        ?.sendMessage(translation.jails.jailHasInmates(jailName).component)
                                } else {
                                    jailApi.deleteJail(jailName)
                                        .onFailure {
                                            ctx.getSender().tryCast<KAudience>()
                                                ?.sendMessage(translation.jails.jailDeleteFail.component)
                                        }
                                        .onSuccess {
                                            ctx.getSender().tryCast<KAudience>()
                                                ?.sendMessage(translation.jails.jailDeleteSuccess(jailName).component)
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
                                        ctx.getSender().tryCast<KAudience>()
                                            ?.sendMessage(translation.jails.inmateFreeFail.component)
                                    }
                                    .onSuccess {
                                        jailController.free(inmate)
                                        cachedJailApi.cache(inmate.uuid)

                                        offlinePlayerToFree.sendMessage(translation.jails.youVeBeenFreed.component)
                                        ctx.getSender().tryCast<KAudience>()
                                            ?.sendMessage(
                                                translation.jails.inmateFreeSuccess(
                                                    offlinePlayerToFree.name.orEmpty()
                                                ).component
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
                                                ctx.getSender()
                                                    .tryCast<KAudience>()
                                                    ?.sendMessage(translation.jails.inmateAddFail.component)
                                            }
                                            .onSuccess {
                                                jailOfflinePlayer.sendMessage(
                                                    translation.jails.youVeBeenJailed(
                                                        jailDuration.toString()
                                                    ).component
                                                )
                                                ctx.getSender().tryCast<KAudience>()?.sendMessage(
                                                    translation.jails.inmateAddSuccess(
                                                        name = jailOfflinePlayer.name.orEmpty(),
                                                        jail = jailName
                                                    ).component
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
