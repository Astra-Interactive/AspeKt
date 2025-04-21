package ru.astrainteractive.aspekt.module.jail.command.jail.executor

import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.jail.command.JailCommandManager
import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.util.toJailLocation
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requireArgument
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission
import ru.astrainteractive.astralibs.command.api.exception.StringDescException
import ru.astrainteractive.astralibs.string.StringDesc

internal fun JailCommandManager.createJail(ctx: BukkitCommandContext) {
    ctx.requirePermission(PluginPermission.JAIL_CREATE)
    val player = ctx.sender as? Player
    player ?: throw StringDescException(StringDesc.Plain("Executor should be player"))
    val jail = Jail(
        name = ctx.requireArgument(1, StringArgumentType),
        location = player.location.toJailLocation()
    )
    val translation = translationKrate.cachedValue
    scope.launch {
        with(kyoriKrate.cachedValue) {
            jailApi.addJail(jail)
                .onFailure {
                    error(it) { "#JailArg.CREATE" }
                    ctx.sender.sendMessage(translation.jails.jailCreatedFail.component)
                }
                .onSuccess {
                    scope.launch {
                        ctx.sender.sendMessage(translation.jails.jailCreatedSuccess(jail.name).component)
                    }
                }
        }
    }
}
