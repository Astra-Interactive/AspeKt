package ru.astrainteractive.aspekt.core.command.api

import com.mojang.brigadier.context.CommandContext

fun interface CommandExceptionHandler {
    fun onException(ctx: CommandContext<Any>, throwable: Throwable)
}
