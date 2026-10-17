package ru.astrainteractive.aspekt.module.economy.command

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.aspekt.module.economy.api.CurrencyCache
import ru.astrainteractive.aspekt.plugin.model.PluginPermission
import ru.astrainteractive.aspekt.plugin.model.PluginTranslation
import ru.astrainteractive.astralibs.command.api.argumenttype.KPlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

/**
 * Ekon command registrar. Builds Brigadier node for:
 * /ekon list
 * /ekon top <currency> [page]
 * /ekon balance <currency> <player>
 * /ekon set <currency> <player> <amount>
 * /ekon add <currency> <player> <amount>
 */
internal class EkonLiteralArgumentBuilder(
    translationKrate: CachedKrate<PluginTranslation>,
    private val currencyCache: CurrencyCache,
    private val executor: EkonCommandExecutor,
    private val multiplatformCommand: MultiplatformCommand,
    private val platformServer: PlatformServer,
    private val commandExceptionHandler: CommandExceptionHandler
) {
    private val translation by translationKrate
    private fun currencyNames(): List<String> = currencyCache.getAllCurrencies().map { currency -> currency.name }

    @Suppress("LongMethod")
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("ekon") {
                runs(commandExceptionHandler::handle) { ctx ->
                    ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                    ctx.getSender().sendMessage(translation.commandError.wrongUsage)
                }
                literal("list") {
                    runs(commandExceptionHandler::handle) { ctx ->
                        ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                        EkonCommand.Model
                            .ListCurrencies(ctx.getSender())
                            .run(executor::execute)
                    }
                }
                literal("top") {
                    argument("currency", StringArgumentType.string()) { currencyArg ->
                        hints { currencyNames() }
                        runs(commandExceptionHandler::handle) { ctx ->
                            ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                            val currencyName = ctx.requireArgument(currencyArg)
                            val currency = currencyCache.getAllCurrencies()
                                .firstOrNull { candidate -> candidate.name.equals(currencyName, true) }
                            if (currency == null) {
                                ctx.getSender().sendMessage(translation.economy.currencyNotFound)
                                return@runs
                            }
                            EkonCommand.Model.Top(
                                sender = ctx.getSender(),
                                currency = currency,
                                page = 0
                            ).run(executor::execute)
                        }
                        argument("page", IntegerArgumentType.integer(0)) { pageArg ->
                            runs(commandExceptionHandler::handle) { ctx ->
                                ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                                val currencyName = ctx.requireArgument(currencyArg)
                                val page = ctx.requireArgument(pageArg)
                                val currency = currencyCache.getAllCurrencies().firstOrNull { candidate ->
                                    candidate.name.equals(
                                        currencyName,
                                        true
                                    )
                                }
                                if (currency == null) {
                                    ctx.getSender().sendMessage(translation.economy.currencyNotFound)
                                    return@runs
                                }
                                EkonCommand.Model.Top(
                                    sender = ctx.getSender(),
                                    currency = currency,
                                    page = page
                                ).run(executor::execute)
                            }
                        }
                    }
                }
                literal("balance") {
                    argument("currency", StringArgumentType.string()) { currencyArg ->
                        hints { currencyNames() }
                        argument("player", StringArgumentType.string()) { playerArg ->
                            hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                            runs(commandExceptionHandler::handle) { ctx ->
                                ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                                val currencyName = ctx.requireArgument(currencyArg)
                                val currency = currencyCache.getAllCurrencies().firstOrNull { candidate ->
                                    candidate.name.equals(
                                        currencyName,
                                        true
                                    )
                                }
                                if (currency == null) {
                                    ctx.getSender().sendMessage(translation.economy.currencyNotFound)
                                    return@runs
                                }
                                val offlinePlayer = ctx.requireArgument(
                                    playerArg,
                                    KPlayerArgumentConverter(platformServer)
                                )
                                EkonCommand.Model.Balance(
                                    sender = ctx.getSender(),
                                    otherPlayer = offlinePlayer,
                                    currency = currency
                                ).run(executor::execute)
                            }
                        }
                    }
                }
                literal("set") {
                    argument("currency", StringArgumentType.string()) { currencyArg ->
                        hints { currencyNames() }
                        argument("player", StringArgumentType.string()) { playerArg ->
                            hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                            argument("amount", DoubleArgumentType.doubleArg()) { amountArg ->
                                runs(commandExceptionHandler::handle) { ctx ->
                                    ctx.requirePermission(PluginPermission.SET_BALANCE)
                                    val currencyName = ctx.requireArgument(currencyArg)
                                    val currency = currencyCache.getAllCurrencies().firstOrNull { candidate ->
                                        candidate.name.equals(
                                            currencyName,
                                            true
                                        )
                                    }
                                    if (currency == null) {
                                        ctx.getSender().sendMessage(translation.economy.currencyNotFound)
                                        return@runs
                                    }
                                    val offlinePlayer = ctx.requireArgument(
                                        playerArg,
                                        KPlayerArgumentConverter(platformServer)
                                    )
                                    val amount: Double = ctx.requireArgument(amountArg)
                                    EkonCommand.Model.Set(
                                        sender = ctx.getSender(),
                                        otherPlayer = offlinePlayer,
                                        currency = currency,
                                        amount = amount
                                    ).run(executor::execute)
                                }
                            }
                        }
                    }
                }
                literal("add") {
                    argument("currency", StringArgumentType.string()) { currencyArg ->
                        hints { currencyNames() }
                        argument("player", StringArgumentType.string()) { playerArg ->
                            hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                            argument("amount", DoubleArgumentType.doubleArg()) { amountArg ->
                                runs(commandExceptionHandler::handle) { ctx ->
                                    ctx.requirePermission(PluginPermission.SET_BALANCE)
                                    val currencyName = ctx.requireArgument(currencyArg)
                                    val currency = currencyCache.getAllCurrencies().firstOrNull { candidate ->
                                        candidate.name.equals(
                                            currencyName,
                                            true
                                        )
                                    }
                                    if (currency == null) {
                                        ctx.getSender().sendMessage(translation.economy.currencyNotFound)
                                        return@runs
                                    }
                                    val offlinePlayer = ctx.requireArgument(
                                        playerArg,
                                        KPlayerArgumentConverter(platformServer)
                                    )
                                    val amount: Double = ctx.requireArgument(amountArg)
                                    EkonCommand.Model.Add(
                                        sender = ctx.getSender(),
                                        otherPlayer = offlinePlayer,
                                        currency = currency,
                                        amount = amount
                                    ).run(executor::execute)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
