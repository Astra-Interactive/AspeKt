package ru.astrainteractive.aspekt.core.forge.command.util

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraftforge.event.RegisterCommandsEvent
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

fun <T> ArgumentBuilder<CommandSourceStack, *>.argument(
    alias: String,
    type: ArgumentType<T>,
    suggests: List<String> = emptyList(),
    builder: (RequiredArgumentBuilder<CommandSourceStack, T>.() -> Unit)? = null,
    execute: (RequiredArgumentBuilder<CommandSourceStack, T>.(CommandContext<CommandSourceStack>) -> Unit)? = null
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
            execute.invoke(argument, commandContext)
            Command.SINGLE_SUCCESS
        }
    }
    then(argument)
}

/**
 * Example:
 * fun RegisterCommandsEvent.giveItemCommand() {
 *     command("giveitem2") {
 *         argument(
 *             alias = "player",
 *             type = StringArgumentType.string(),
 *             builder = {
 *                 argument(
 *                     alias = "truefalse",
 *                     type = PrimitiveArgumentType.Boolean.toBrigadier(),
 *                     builder = {
 *                         argument(
 *                             alias = "amount",
 *                             type = IntegerArgumentType.integer(1, 4),
 *                             execute = {
 *                                 println("Player -> Item -> amount")
 *                             }
 *                         )
 *                     },
 *                     execute = {
 *                         println("Player -> Item")
 *                     }
 *                 )
 *             },
 *         )
 *     }
 * }
 */
fun RegisterCommandsEvent.command(alias: String, block: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit) {
    val literal = Commands.literal(alias)
    runCatching {
        literal.block()
    }.onFailure { println("Catched exception: ${it.localizedMessage}") }
    dispatcher.register(literal)
}
