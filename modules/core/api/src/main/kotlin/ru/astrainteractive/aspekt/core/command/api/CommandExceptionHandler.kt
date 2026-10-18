package ru.astrainteractive.aspekt.core.command.api

import com.mojang.brigadier.context.CommandContext

fun interface CommandExceptionHandler {
    fun handle(ctx: CommandContext<Any>, throwable: Throwable)
}
