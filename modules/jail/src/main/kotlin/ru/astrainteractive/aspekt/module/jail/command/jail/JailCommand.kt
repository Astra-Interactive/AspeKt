package ru.astrainteractive.aspekt.module.jail.command.jail

import ru.astrainteractive.aspekt.module.jail.command.JailCommandManager
import ru.astrainteractive.aspekt.module.jail.command.jail.executor.createJail
import ru.astrainteractive.aspekt.module.jail.command.jail.executor.deleteJail
import ru.astrainteractive.aspekt.module.jail.command.jail.executor.freeFromJail
import ru.astrainteractive.aspekt.module.jail.command.jail.executor.inmateIntoJail
import ru.astrainteractive.aspekt.module.jail.command.jail.model.JailArg
import ru.astrainteractive.astralibs.command.api.argumenttype.EnumArgumentType
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requireArgument
import ru.astrainteractive.astralibs.command.api.util.PluginExt.setCommandExecutor

/**
 * /jail list
 * /jail create <JAIL>
 * /jail delete <JAIL>
 * /jail inmate <JAIL> <PLAYER> <TIME>
 */
internal fun JailCommandManager.jail() = plugin.setCommandExecutor(
    alias = "jail",
    commandExecutor = commandExecutor@{ ctx ->
        val jailArg = ctx.requireArgument(0, EnumArgumentType(JailArg.entries))
        when (jailArg) {
            JailArg.LIST -> listJails(ctx)

            JailArg.CREATE -> createJail(ctx)

            JailArg.DELETE -> deleteJail(ctx)

            JailArg.INMATE -> inmateIntoJail(ctx)

            JailArg.FREE -> freeFromJail(ctx)
        }
    },
    errorHandler = { ctx, throwable ->
        val translation = translationKrate.cachedValue
        val kyori = kyoriKrate.cachedValue
        with(kyori) {
            val message = translation.general
                .commandError(throwable.message ?: throwable.localizedMessage)
                .component
            ctx.sender.sendMessage(message)
        }
        error(throwable) { "could not execute jail command" }
    }
)
