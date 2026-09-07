package ru.astrainteractive.aspekt.module.sethome.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.aspekt.module.sethome.model.PlayerHome
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.brigadier.sender.KPlayerKCommandSender
import ru.astrainteractive.astralibs.command.api.exception.NoPermissionException
import ru.astrainteractive.astralibs.command.api.exception.NotPlayerExecutorException
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.permission.Permission
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import ru.astrainteractive.klibs.mikro.core.util.tryCast

/**
 * Platform-agnostic SetHome command registrar. Registers:
 * - /sethome <home_name> [force]
 * - /delhome <home_name>
 * - /home <home_name>
 *
 * Every node is player-only and guarded by its own [PluginPermission] node.
 */
@Suppress("LongParameterList")
internal class SetHomeCommandRegistrar(
    private val homeKrateProvider: HomeKrateProvider,
    private val executor: HomeCommandExecutor,
    private val multiplatformCommand: MultiplatformCommand,
    private val registrarContext: CommandRegistrarContext,
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>
) : KyoriComponentSerializer by kyoriKrate.unwrap(),
    Logger by JUtiltLogger("AspeKt-SetHomeCommandRegistrar") {
    private val translation by translationKrate

    /**
     * [MultiplatformCommand.runs] never lets an exception reach Brigadier, so a rejected
     * command stays silent unless the sender is told why here.
     */
    private fun reportFailure(ctx: CommandContext<Any>, throwable: Throwable) {
        val sender = with(multiplatformCommand) { ctx.getSender() }
        when (throwable) {
            is NoPermissionException -> sender.sendMessage(translation.general.noPermission.component)
            is NotPlayerExecutorException -> sender.sendMessage(translation.general.onlyPlayerCommand.component)
            else -> error(throwable) { "Could not execute home command" }
        }
    }

    private fun homeNameHints(ctx: CommandContext<Any>, permission: Permission): List<String> {
        val player = with(multiplatformCommand) { ctx.getSender() }
            .tryCast<KPlayerKCommandSender>()
            ?.takeIf { sender -> sender.hasPermission(permission) }
            ?.instance
            ?: return emptyList()
        return homeKrateProvider.get(player.uuid)
            .cachedStateFlow
            .value
            .map(PlayerHome::name)
    }

    private fun executeSetHome(
        ctx: CommandContext<Any>,
        homeNameArg: MultiplatformCommand.BrigadierArgument<String>,
        force: Boolean
    ) {
        with(multiplatformCommand) {
            ctx.requirePermission(PluginPermission.SET_HOME)
            val player = ctx.requirePlayer()
            HomeCommand.SetHome(
                playerData = player,
                playerHome = PlayerHome(
                    location = player.getLocation(),
                    name = ctx.requireArgument(homeNameArg)
                ),
                force = force
            ).run(executor::execute)
        }
    }

    private fun createSetHomeNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("sethome") {
                argument("home_name", StringArgumentType.string()) { homeNameArg ->
                    runs(onFailure = ::reportFailure) { ctx ->
                        executeSetHome(ctx, homeNameArg, force = false)
                    }
                    literal("force") {
                        runs(onFailure = ::reportFailure) { ctx ->
                            executeSetHome(ctx, homeNameArg, force = true)
                        }
                    }
                }
            }
        }
    }

    private fun createDelHomeNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("delhome") {
                argument("home_name", StringArgumentType.string()) { homeNameArg ->
                    hints { ctx -> homeNameHints(ctx, PluginPermission.DEL_HOME) }
                    runs(onFailure = ::reportFailure) { ctx ->
                        ctx.requirePermission(PluginPermission.DEL_HOME)
                        HomeCommand.DelHome(
                            playerData = ctx.requirePlayer(),
                            homeName = ctx.requireArgument(homeNameArg)
                        ).run(executor::execute)
                    }
                }
            }
        }
    }

    private fun createHomeNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("home") {
                argument("home_name", StringArgumentType.string()) { homeNameArg ->
                    hints { ctx -> homeNameHints(ctx, PluginPermission.HOME) }
                    runs(onFailure = ::reportFailure) { ctx ->
                        ctx.requirePermission(PluginPermission.HOME)
                        HomeCommand.TpHome(
                            playerData = ctx.requirePlayer(),
                            homeName = ctx.requireArgument(homeNameArg)
                        ).run(executor::execute)
                    }
                }
            }
        }
    }

    fun register() {
        listOf(
            createSetHomeNode(),
            createDelHomeNode(),
            createHomeNode()
        ).forEach(registrarContext::registerWhenReady)
    }
}
