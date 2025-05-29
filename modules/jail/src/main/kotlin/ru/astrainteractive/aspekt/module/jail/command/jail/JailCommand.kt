package ru.astrainteractive.aspekt.module.jail.command.jail

import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.asUnboxed
import ru.astrainteractive.aspekt.command.command
import ru.astrainteractive.aspekt.command.hints
import ru.astrainteractive.aspekt.command.literal
import ru.astrainteractive.aspekt.command.requireArgument
import ru.astrainteractive.aspekt.command.requirePermission
import ru.astrainteractive.aspekt.command.runs
import ru.astrainteractive.aspekt.command.stringArgument
import ru.astrainteractive.aspekt.module.jail.command.JailCommandManager
import ru.astrainteractive.aspekt.module.jail.command.argumenttype.DurationArgumentType
import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.aspekt.module.jail.util.sendMessage
import ru.astrainteractive.aspekt.module.jail.util.toJailLocation
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.OfflinePlayerArgument
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.command.api.exception.StringDescException
import ru.astrainteractive.astralibs.string.StringDesc
import java.time.Instant

@Suppress("LongMethod")
internal fun JailCommandManager.jailCommand() {
    with(kyoriKrate.asUnboxed()) {
        command("jail") {
            literal("list") {
                runs { ctx ->
                    ctx.requirePermission(PluginPermission.JAIL_LIST)
                    val translation = translationKrate.cachedValue
                    scope.launch {
                        val jails = jailApi.getJails().getOrNull().orEmpty().map(Jail::name)
                        val jailsString = jails.joinToString()
                        ctx.source.sender.sendMessage(translation.jails.jailsList(jailsString).component)
                    }
                }
            }
            literal("create") {
                stringArgument("jail") {
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.JAIL_CREATE)
                        val player = ctx.source.sender as? Player
                        player ?: throw StringDescException(StringDesc.Plain("Executor should be player"))
                        val jail = Jail(
                            name = ctx.requireArgument("jail", StringArgumentType),
                            location = player.location.toJailLocation()
                        )
                        val translation = translationKrate.cachedValue
                        scope.launch {
                            jailApi.addJail(jail)
                                .onFailure {
                                    error(it) { "#JailArg.CREATE" }
                                    ctx.source.sender.sendMessage(translation.jails.jailCreatedFail.component)
                                }
                                .onSuccess {
                                    scope.launch {
                                        ctx.source.sender.sendMessage(
                                            translation.jails.jailCreatedSuccess(jail.name).component
                                        )
                                    }
                                }
                        }
                    }
                }
            }
            literal("delete") {
                stringArgument("jail") {
                    hints(cachedJailApi.getJails().map(Jail::name))
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.JAIL_DELETE)
                        val translation = translationKrate.cachedValue
                        scope.launch {
                            val jailName = ctx.requireArgument("jail", StringArgumentType)
                            if (jailApi.getJailInmates(jailName).getOrNull().orEmpty().isNotEmpty()) {
                                ctx.source.sender.sendMessage(translation.jails.jailHasInmates(jailName).component)
                            } else {
                                jailApi.deleteJail(jailName)
                                    .onFailure {
                                        ctx.source.sender.sendMessage(translation.jails.jailDeleteFail.component)
                                    }
                                    .onSuccess {
                                        scope.launch {
                                            ctx.source.sender.sendMessage(
                                                translation.jails.jailDeleteSuccess(jailName).component
                                            )
                                        }
                                    }
                            }
                        }
                    }
                }
            }
            literal("free") {
                stringArgument("player") {
                    hints(Bukkit.getOnlinePlayers().map(Player::getName))
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.JAIL_FREE)
                        val translation = translationKrate.cachedValue
                        scope.launch {
                            val offlinePlayerToFree = ctx.requireArgument("player", OfflinePlayerArgument)
                            val inmate = jailApi.getInmate(offlinePlayerToFree.uniqueId.toString())
                                .getOrNull()
                                ?: error("Could not find jail inmate!")

                            jailApi.free(offlinePlayerToFree.uniqueId.toString())
                                .onFailure {
                                    error(it) { "#JailArg.CREATE" }
                                    ctx.source.sender.sendMessage(translation.jails.inmateFreeFail.component)
                                }
                                .onSuccess {
                                    jailController.free(inmate)
                                    cachedJailApi.cache(inmate.uuid)

                                    offlinePlayerToFree.sendMessage(translation.jails.youVeBeenFreed.component)
                                    ctx.source.sender.sendMessage(
                                        translation.jails.inmateFreeSuccess(
                                            name = offlinePlayerToFree.name.orEmpty(),
                                        ).component
                                    )
                                }
                        }
                    }
                }
            }
            literal("inmate") {
                stringArgument("jail") {
                    hints(cachedJailApi.getJails().map(Jail::name))
                    stringArgument("player") {
                        hints(Bukkit.getOnlinePlayers().map(Player::getName))
                        stringArgument("time") {
                            hints(listOf("TIME:1s,1m,1h10m"))
                            runs { ctx ->
                                ctx.requirePermission(PluginPermission.JAIL_INMATE)
                                val translation = translationKrate.cachedValue
                                scope.launch {
                                    val jailName = ctx.requireArgument("jail", StringArgumentType)
                                    val jailOfflinePlayer = ctx.requireArgument("player", OfflinePlayerArgument)
                                    val jailDuration = ctx.requireArgument("time", DurationArgumentType)

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
                                            ctx.source.sender.sendMessage(translation.jails.inmateAddFail.component)
                                        }
                                        .onSuccess {
                                            scope.launch {
                                                jailOfflinePlayer.sendMessage(
                                                    translation.jails.youVeBeenJailed(jailDuration.toString()).component
                                                )
                                                ctx.source.sender.sendMessage(
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
