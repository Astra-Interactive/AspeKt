package ru.astrainteractive.aspekt.module.jail.command

import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.jail.command.argumenttype.DurationArgumentType
import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.aspekt.module.jail.util.sendMessage
import ru.astrainteractive.aspekt.module.jail.util.toJailLocation
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.EnumArgument
import ru.astrainteractive.astralibs.command.api.argumenttype.EnumArgumentType
import ru.astrainteractive.astralibs.command.api.argumenttype.OfflinePlayerArgument
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.command.api.command.BukkitTabCompleter
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.argumentOrElse
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requireArgument
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission
import ru.astrainteractive.astralibs.command.api.exception.StringDescException
import ru.astrainteractive.astralibs.command.api.util.PluginExt.setCommandExecutor
import ru.astrainteractive.astralibs.command.api.util.PluginExt.setCommandTabCompleter
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.util.StringListExt.withEntry
import java.time.Instant

internal enum class JailArg(override val value: String) : EnumArgument {
    LIST("list"),
    CREATE("create"),
    DELETE("delete"),
    INMATE("inmate"),
    FREE("free")
}

private fun JailCommandManager.listJails(ctx: BukkitCommandContext) {
    ctx.requirePermission(PluginPermission.Jail.JailList)
    val translation = translationKrate.cachedValue
    scope.launch {
        with(kyoriKrate.cachedValue) {
            val jails = jailApi.getJails().getOrNull().orEmpty().map(Jail::name)
            val jailsString = jails.joinToString()
            ctx.sender.sendMessage(translation.jails.jailsList(jailsString).component)
        }
    }
}

private fun JailCommandManager.createJail(ctx: BukkitCommandContext) {
    ctx.requirePermission(PluginPermission.Jail.JailCreate)
    val player = ctx.sender as? Player
    player ?: throw StringDescException(StringDesc.Plain("Executor should be player"))
    val jail = Jail(
        name = ctx.requireArgument(1, StringArgumentType),
        location = player.location.toJailLocation()
    )
    val translation = translationKrate.cachedValue
    scope.launch {
        with(kyoriKrate.cachedValue) {
            jailApi.addJail(jail)
                .onFailure {
                    error(it) { "#JailArg.CREATE" }
                    ctx.sender.sendMessage(translation.jails.jailCreatedFail.component)
                }
                .onSuccess {
                    scope.launch {
                        ctx.sender.sendMessage(translation.jails.jailCreatedSuccess(jail.name).component)
                    }
                }
        }
    }
}

private fun JailCommandManager.deleteJail(ctx: BukkitCommandContext) {
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

private fun JailCommandManager.inmateIntoJail(ctx: BukkitCommandContext) {
    ctx.requirePermission(PluginPermission.Jail.JailInmate)
    val translation = translationKrate.cachedValue
    scope.launch {
        val jailName = ctx.requireArgument(1, StringArgumentType)
        val jailOfflinePlayer = ctx.requireArgument(2, OfflinePlayerArgument)
        val jailDuration = ctx.requireArgument(3, DurationArgumentType)

        with(kyoriKrate.cachedValue) {
            val inmate = JailInmate(
                uuid = jailOfflinePlayer.uniqueId.toString(),
                jailName = jailName,
                start = Instant.now(),
                duration = jailDuration,
                lastLocation = jailOfflinePlayer.location
                    ?.toJailLocation()
                    ?: Bukkit.getWorlds().first().spawnLocation.toJailLocation()
            )
            jailApi.addInmate(inmate)
                .onFailure {
                    ctx.sender.sendMessage(translation.jails.inmateAddFail.component)
                }
                .onSuccess {
                    scope.launch {
                        jailOfflinePlayer.sendMessage(
                            translation.jails.youVeBeenJailed(jailDuration.toString()).component
                        )
                        ctx.sender.sendMessage(
                            translation.jails.inmateAddSuccess(
                                name = jailOfflinePlayer.name.orEmpty(),
                                jail = jailName
                            ).component
                        )

                        cachedJailApi.cache(inmate.uuid)
                        jailController.onJailed(inmate)
                    }
                }
        }
    }
}

private fun JailCommandManager.freeFromJail(ctx: BukkitCommandContext) {
    ctx.requirePermission(PluginPermission.Jail.JailFree)
    val translation = translationKrate.cachedValue
    scope.launch {
        with(kyoriKrate.cachedValue) {
            val offlinePlayerToFree = ctx.requireArgument(1, OfflinePlayerArgument)
            jailApi.free(offlinePlayerToFree.uniqueId.toString())
                .onFailure {
                    error(it) { "#JailArg.CREATE" }
                    ctx.sender.sendMessage(translation.jails.inmateFreeFail.component)
                }
                .onSuccess {
                    val inmate = jailApi.getInmate(offlinePlayerToFree.uniqueId.toString())
                        .getOrNull()
                        ?: error("Could not find jail inmate!")
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

private fun BukkitCommandContext.withArgument(
    index: Int,
    hints: List<String>,
    block: () -> List<String>? = { null }
): List<String>? {
    return block.invoke() ?: args.getOrNull(index)?.let { entry -> hints.withEntry(entry) }
}

private fun BukkitCommandContext.runArgument(
    index: Int,
    value: String,
    block: () -> Unit
) {
    if (args.getOrNull(index) != value) return
    block.invoke()
}

internal fun JailCommandManager.jailTabCompleter() = plugin.setCommandTabCompleter(
    alias = "jail",
    tabCompleter = BukkitTabCompleter { ctx ->
        ctx.withArgument(
            index = 0,
            hints = JailArg.entries.map(JailArg::value),
            block = {
                val arg = ctx.argumentOrElse(
                    index = 0,
                    type = EnumArgumentType(JailArg.entries),
                    default = { JailArg.LIST }
                )
                when (arg) {
                    JailArg.LIST -> ctx.withArgument(
                        index = 1,
                        hints = listOf()
                    )

                    JailArg.CREATE -> ctx.withArgument(
                        index = 1,
                        hints = listOf("JAIL_NAME")
                    )

                    JailArg.DELETE -> listOf("JAIL_NAME")
                    JailArg.INMATE -> ctx.withArgument(
                        index = 1,
                        hints = listOf("JAIL_NAME"),
                        block = {
                            ctx.withArgument(
                                index = 2,
                                hints = listOf("USER_NAME"),
                                block = {
                                    ctx.withArgument(
                                        index = 3,
                                        hints = listOf("TIME:1s,1m,1h10m")
                                    )
                                }
                            )
                        }
                    )

                    JailArg.FREE -> listOf("USER_NAME")
                }
            }
        ).orEmpty()
    }
)

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
        println("Executing jail $jailArg")
        when (jailArg) {
            JailArg.LIST -> listJails(ctx)

            JailArg.CREATE -> createJail(ctx)

            JailArg.DELETE -> deleteJail(ctx)

            JailArg.INMATE -> inmateIntoJail(ctx)

            JailArg.FREE -> freeFromJail(ctx)
        }
    },
    errorHandler = { ctx, throwable ->
        error(throwable) { "could not execute jail command" }
    }
)
