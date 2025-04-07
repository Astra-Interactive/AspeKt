package ru.astrainteractive.aspekt.core.forge.command.util

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.MinecraftServer
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.rcon.RconConsoleSource
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.context.ForgeCommandContext
import ru.astrainteractive.aspekt.core.forge.permission.toPermissible
import ru.astrainteractive.astralibs.command.api.error.ErrorHandler
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException
import ru.astrainteractive.astralibs.command.api.exception.CommandException
import ru.astrainteractive.astralibs.permission.Permission

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

fun CommandContext<CommandSourceStack>.requirePermission(permission: Permission): Boolean {
    if (source.source is RconConsoleSource) return true
    if (source.source is DedicatedServer) return true
    if (source.source is MinecraftServer) return true
    val serverPlayer = source.entity as? ServerPlayer ?: run {
        throw CommandException("$source.source; is not a player!")
    }
    return serverPlayer.toPermissible().hasPermission(permission)
}

fun ArgumentBuilder<CommandSourceStack, *>.stringArgument(
    alias: String,
    suggests: List<String> = emptyList(),
    errorHandler: ErrorHandler<ForgeCommandContext> = ErrorHandler { _, _ -> },
    builder: (RequiredArgumentBuilder<CommandSourceStack, String>.() -> Unit)? = null,
    execute: (RequiredArgumentBuilder<CommandSourceStack, String>.(CommandContext<CommandSourceStack>) -> Unit)? = null,
) = argument(
    alias = alias,
    type = StringArgumentType.string(),
    suggests = suggests,
    builder = builder,
    execute = execute,
    errorHandler = errorHandler
)

@Suppress("LongParameterList")
fun <T> ArgumentBuilder<CommandSourceStack, *>.argument(
    alias: String,
    type: ArgumentType<T>,
    suggests: List<String> = emptyList(),
    errorHandler: ErrorHandler<ForgeCommandContext> = ErrorHandler { _, _ -> },
    builder: (RequiredArgumentBuilder<CommandSourceStack, T>.() -> Unit)? = null,
    execute: (RequiredArgumentBuilder<CommandSourceStack, T>.(CommandContext<CommandSourceStack>) -> Unit)? = null,
): ArgumentBuilder<CommandSourceStack, *> {
    val requiredArgumentBuilder: RequiredArgumentBuilder<CommandSourceStack, T> = Commands
        .argument(alias, type)
        .suggests { context, builder ->
            suggests.forEach { suggestion ->
                builder.suggest(suggestion)
            }
            builder.buildFuture()
        }
    builder?.invoke(requiredArgumentBuilder)
    execute?.let {
        requiredArgumentBuilder.executes { commandContext ->
            runCatching { execute.invoke(requiredArgumentBuilder, commandContext) }
                .onFailure { throwable ->
                    errorHandler.handle(
                        ctx = ForgeCommandContext(commandContext),
                        throwable = throwable
                    )
                }
            Command.SINGLE_SUCCESS
        }
    }
    return then(requiredArgumentBuilder)
}

fun RegisterCommandsEvent.command(
    alias: String,
    block: ArgumentBuilder<CommandSourceStack, *>.() -> Unit
) {
    val literal = Commands.literal(alias)
    literal.block()
    dispatcher.register(literal)
}

fun literal(name: String): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal(name)
}

fun <T> argument(
    alias: String,
    type: ArgumentType<T>,
    suggests: List<String> = emptyList(),
    errorHandler: ErrorHandler<ForgeCommandContext> = ErrorHandler { _, _ -> },
    execute: ((CommandContext<CommandSourceStack>) -> Unit)? = null,
): RequiredArgumentBuilder<CommandSourceStack, T> {
    val argument = Commands.argument(alias, type)
    if (suggests.isNotEmpty()) {
        argument.suggests { context, builder ->
            suggests.forEach { suggestion ->
                builder.suggest(suggestion)
            }
            builder.buildFuture()
        }
    }
    execute?.let {
        argument.executes {
            runCatching { execute.invoke(it) }
                .onFailure { throwable ->
                    errorHandler.handle(
                        ctx = ForgeCommandContext(it),
                        throwable = throwable
                    )
                }
            Command.SINGLE_SUCCESS
        }
    }
    return argument
}
