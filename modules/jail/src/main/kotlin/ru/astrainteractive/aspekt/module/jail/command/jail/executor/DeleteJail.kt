package ru.astrainteractive.aspekt.module.jail.command.jail.executor

import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.jail.command.JailCommandManager
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requireArgument
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission

internal fun JailCommandManager.deleteJail(ctx: BukkitCommandContext) {
    ctx.requirePermission(PluginPermission.Jail.JailDelete)
    val translation = translationKrate.cachedValue
    scope.launch {
        val jailName = ctx.requireArgument(1, StringArgumentType)
        with(kyoriKrate.cachedValue) {
            if (jailApi.getJailInmates(jailName).getOrNull().orEmpty().isNotEmpty()) {
                ctx.sender.sendMessage(translation.jails.jailHasInmates(jailName).component)
            } else {
                jailApi.deleteJail(jailName)
                    .onFailure {
                        ctx.sender.sendMessage(translation.jails.jailDeleteFail.component)
                    }
                    .onSuccess {
                        scope.launch {
                            ctx.sender.sendMessage(translation.jails.jailDeleteSuccess(jailName).component)
                        }
                    }
            }
        }
    }
}
