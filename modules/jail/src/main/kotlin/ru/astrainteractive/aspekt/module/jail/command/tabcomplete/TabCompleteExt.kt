package ru.astrainteractive.aspekt.module.jail.command.tabcomplete

import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.util.withEntry

internal fun BukkitCommandContext.withArgument(
    index: Int,
    hints: List<String>,
    block: () -> List<String>? = { null }
): List<String>? {
    return block.invoke() ?: args.getOrNull(index)?.let { entry -> hints.withEntry(entry) }
}

internal fun BukkitCommandContext.runArgument(
    index: Int,
    value: String,
    block: () -> Unit
) {
    if (args.getOrNull(index) != value) return
    block.invoke()
}
