package ru.astrainteractive.aspekt.module.jail.command.jail

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
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
import ru.astrainteractive.astralibs.command.api.util.argument
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.hints
import ru.astrainteractive.astralibs.command.api.util.literal
import ru.astrainteractive.astralibs.command.api.util.requireArgument
import ru.astrainteractive.astralibs.command.api.util.requirePermission
import ru.astrainteractive.astralibs.command.api.util.requirePlayer
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import java.time.Instant

/**
 * /jail list
 * /jail create <jail>
 * /jail delete <jail>
 * /jail free <player>
 * /jail inmate <jail> <player> <time>
 */
internal class JailCommandRegistrar(
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val scope: CoroutineScope,
    private val jailApi: JailApi,
    private val cachedJailApi: CachedJailApi,
    private val jailController: ru.astrainteractive.aspekt.module.jail.controller.JailController
) {
    private val translation by translationKrate
    private val kyori by kyoriKrate
    fun createNode(): LiteralCommandNode<CommandSourceStack> {
        return command("jail") {
            literal("list") {
                runs { ctx ->
                    ctx.requirePermission(PluginPermission.JAIL_LIST)
                    scope.launch {
                        val jails = jailApi.getJails().getOrNull().orEmpty().map(Jail::name)
                        val jailsString = jails.joinToString()
                        ctx.source.sender.sendMessage(kyori.toComponent(translation.jails.jailsList(jailsString)))
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
                            location = player.location.toJailLocation()
                        )
                        scope.launch {
                            jailApi.addJail(jail)
                                .onFailure {
                                    ctx.source.sender.sendMessage(kyori.toComponent(translation.jails.jailCreatedFail))
                                }
                                .onSuccess {
                                    ctx.source.sender.sendMessage(
                                        kyori.toComponent(translation.jails.jailCreatedSuccess(jail.name))
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
                                ctx.source.sender.sendMessage(
                                    kyori.toComponent(translation.jails.jailHasInmates(jailName))
                                )
                            } else {
                                jailApi.deleteJail(jailName)
                                    .onFailure {
                                        ctx.source.sender.sendMessage(
                                            kyori.toComponent(translation.jails.jailDeleteFail)
                                        )
                                    }
                                    .onSuccess {
                                        ctx.source.sender.sendMessage(
                                            kyori.toComponent(translation.jails.jailDeleteSuccess(jailName))
                                        )
                                    }
                            }
                        }
                    }
                }
            }
            literal("free") {
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.JAIL_FREE)
                        scope.launch {
                            val offlinePlayerToFree = ctx.requireArgument(playerArg, OfflinePlayerArgumentConverter)
                            val inmate = jailApi.getInmate(offlinePlayerToFree.uniqueId.toString())
                                .getOrNull()
                                ?: return@launch

                            jailApi.free(offlinePlayerToFree.uniqueId.toString())
                                .onFailure {
                                    ctx.source.sender.sendMessage(kyori.toComponent(translation.jails.inmateFreeFail))
                                }
                                .onSuccess {
                                    jailController.free(inmate)
                                    cachedJailApi.cache(inmate.uuid)

                                    offlinePlayerToFree.sendMessage(kyori.toComponent(translation.jails.youVeBeenFreed))
                                    ctx.source.sender.sendMessage(
                                        kyori.toComponent(
                                            translation.jails.inmateFreeSuccess(
                                                name = offlinePlayerToFree.name.orEmpty(),
                                            )
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
                        hints { Bukkit.getOnlinePlayers().map(Player::getName) }
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
                                            ctx.source.sender.sendMessage(
                                                kyori.toComponent(translation.jails.inmateAddFail)
                                            )
                                        }
                                        .onSuccess {
                                            jailOfflinePlayer.sendMessage(
                                                kyori.toComponent(
                                                    translation.jails.youVeBeenJailed(jailDuration.toString())
                                                )
                                            )
                                            ctx.source.sender.sendMessage(
                                                kyori.toComponent(
                                                    translation.jails.inmateAddSuccess(
                                                        name = jailOfflinePlayer.name.orEmpty(),
                                                        jail = jailName
                                                    )
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
        }.build()
    }
}
