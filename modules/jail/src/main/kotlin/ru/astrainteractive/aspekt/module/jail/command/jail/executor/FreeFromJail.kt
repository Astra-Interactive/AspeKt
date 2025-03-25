package ru.astrainteractive.aspekt.module.jail.command.jail.executor

import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.jail.command.JailCommandManager
import ru.astrainteractive.aspekt.module.jail.util.sendMessage
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.OfflinePlayerArgument
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requireArgument
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission

internal fun JailCommandManager.freeFromJail(ctx: BukkitCommandContext) {
    ctx.requirePermission(PluginPermission.Jail.JailFree)
    val translation = translationKrate.cachedValue
    scope.launch {
        with(kyoriKrate.cachedValue) {
            val offlinePlayerToFree = ctx.requireArgument(1, OfflinePlayerArgument)
            val inmate = jailApi.getInmate(offlinePlayerToFree.uniqueId.toString())
                .getOrNull()
                ?: error("Could not find jail inmate!")

            jailApi.free(offlinePlayerToFree.uniqueId.toString())
                .onFailure {
                    error(it) { "#JailArg.CREATE" }
                    ctx.sender.sendMessage(translation.jails.inmateFreeFail.component)
                }
                .onSuccess {
                    jailController.free(inmate)
                    cachedJailApi.cache(inmate.uuid)

                    offlinePlayerToFree.sendMessage(translation.jails.youVeBeenFreed.component)
                    ctx.sender.sendMessage(
                        translation.jails.inmateFreeSuccess(
                            name = offlinePlayerToFree.name.orEmpty(),
                        ).component
                    )
                }
        }
    }
}
