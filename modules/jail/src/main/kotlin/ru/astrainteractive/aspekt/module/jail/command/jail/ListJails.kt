package ru.astrainteractive.aspekt.module.jail.command.jail

import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.jail.command.JailCommandManager
import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission

internal fun JailCommandManager.listJails(ctx: BukkitCommandContext) {
    ctx.requirePermission(PluginPermission.JAIL_LIST)
    val translation = translationKrate.cachedValue
    scope.launch {
        with(kyoriKrate.cachedValue) {
            val jails = jailApi.getJails().getOrNull().orEmpty().map(Jail::name)
            val jailsString = jails.joinToString()
            ctx.sender.sendMessage(translation.jails.jailsList(jailsString).component)
        }
    }
}
