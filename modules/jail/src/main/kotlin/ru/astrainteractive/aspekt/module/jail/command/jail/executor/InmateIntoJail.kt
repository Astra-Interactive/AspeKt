package ru.astrainteractive.aspekt.module.jail.command.jail.executor

import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.module.jail.command.JailCommandManager
import ru.astrainteractive.aspekt.module.jail.command.argumenttype.DurationArgumentType
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.aspekt.module.jail.util.sendMessage
import ru.astrainteractive.aspekt.module.jail.util.toJailLocation
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.OfflinePlayerArgument
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requireArgument
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission
import java.time.Instant

internal fun JailCommandManager.inmateIntoJail(ctx: BukkitCommandContext) {
    ctx.requirePermission(PluginPermission.Jail.JailInmate)
    val translation = translationKrate.cachedValue
    scope.launch {
        val jailName = ctx.requireArgument(1, StringArgumentType)
        val jailOfflinePlayer = ctx.requireArgument(2, OfflinePlayerArgument)
        val jailDuration = ctx.requireArgument(3, DurationArgumentType)

        with(kyoriKrate.cachedValue) {
            val inmate = JailInmate(
                uuid = jailOfflinePlayer.uniqueId.toString(),
                jailName = jailName,
                start = Instant.now(),
                duration = jailDuration,
                lastLocation = jailOfflinePlayer.location
                    ?.toJailLocation()
                    ?: Bukkit.getWorlds().first().spawnLocation.toJailLocation()
            )
            jailApi.addInmate(inmate)
                .onFailure {
                    ctx.sender.sendMessage(translation.jails.inmateAddFail.component)
                }
                .onSuccess {
                    scope.launch {
                        jailOfflinePlayer.sendMessage(
                            translation.jails.youVeBeenJailed(jailDuration.toString()).component
                        )
                        ctx.sender.sendMessage(
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
