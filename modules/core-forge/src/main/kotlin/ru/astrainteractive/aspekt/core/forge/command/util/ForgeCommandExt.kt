package ru.astrainteractive.aspekt.core.forge.command.util

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.context.ForgeCommandBuilderContext
import ru.astrainteractive.aspekt.core.forge.command.context.ForgeCommandContext
import ru.astrainteractive.astralibs.command.api.error.ErrorHandler
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException

fun <T : Any> CommandContext<CommandSourceStack>.requireArgument(
    alias: String,
    type: ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentType<T>
): T {
    val raw = getArgument(alias, String::class.java)
    return raw?.let(type::transform) ?: throw BadArgumentException(raw, type)
}

fun <T : Any> CommandContext<CommandSourceStack>.findArgument(
    alias: String,
    type: ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentType<T>
): T? {
    val raw = getArgument(alias, String::class.java)
    return runCatching { raw?.let(type::transform) }.getOrNull()
}

fun <T : Any> CommandContext<CommandSourceStack>.argumentOrElse(
    alias: String,
    type: ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentType<T>,
    default: () -> T
): T {
    return findArgument(alias, type) ?: default.invoke()
}

fun <T> ForgeCommandBuilderContext.argument(
    alias: String,
    type: ArgumentType<T>,
    suggests: List<String> = emptyList(),
    builder: (RequiredArgumentBuilder<CommandSourceStack, T>.() -> Unit)? = null,
    execute: (RequiredArgumentBuilder<CommandSourceStack, T>.(CommandContext<CommandSourceStack>) -> Unit)? = null,
) {
    val argument: RequiredArgumentBuilder<CommandSourceStack, T> = Commands
        .argument(alias, type)
        .suggests { context, builder ->
            suggests.forEach { suggestion ->
                builder.suggest(suggestion)
            }
            builder.buildFuture()
        }
    builder?.invoke(argument)
    execute?.let {
        argument.executes { commandContext ->
            runCatching { execute.invoke(argument, commandContext) }
                .onFailure { throwable ->
                    errorHandler.handle(
                        ctx = ForgeCommandContext(commandContext),
                        throwable = throwable
                    )
                }
            Command.SINGLE_SUCCESS
        }
    }
    this.builder.then(argument)
}

fun RegisterCommandsEvent.command(
    alias: String,
    errorHandler: ErrorHandler<ForgeCommandContext> = ErrorHandler { _, _ -> },
    block: ForgeCommandBuilderContext.() -> Unit
) {
    val literal = Commands.literal(alias)
    val context = ForgeCommandBuilderContext(literal, errorHandler)
    context.block()
    dispatcher.register(literal)
}
