package ru.astrainteractive.aspekt.module.economy.command.ekon

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.economy.database.dao.CachedDao
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.argumenttype.KPlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

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
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val cachedDao: CachedDao,
    private val executor: EkonCommandExecutor,
    private val multiplatformCommand: MultiplatformCommand,
    private val platformServer: PlatformServer
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate
    private fun currencyNames(): List<String> = cachedDao.getAllCurrencies().map { it.name }

    @Suppress("LongMethod")
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("ekon") {
                runs { ctx ->
                    ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                    ctx.getSender().sendMessage(translation.general.wrongUsage.component)
                }
                literal("list") {
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                        EkonCommand.Model
                            .ListCurrencies(ctx.getSender())
                            .run(executor::execute)
                    }
                }
                literal("top") {
                    argument("currency", StringArgumentType.string()) { currencyArg ->
                        hints { currencyNames() }
                        runs { ctx ->
                            ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                            val currencyName = ctx.requireArgument(currencyArg)
                            val currency =
                                cachedDao.getAllCurrencies().firstOrNull { it.name.equals(currencyName, true) }
                            if (currency == null) {
                                ctx.getSender().sendMessage(translation.economy.currencyNotFound.component)
                                return@runs
                            }
                            EkonCommand.Model.Top(
                                sender = ctx.getSender(),
                                currency = currency,
                                page = 0
                            ).run(executor::execute)
                        }
                        argument("page", IntegerArgumentType.integer(0)) { pageArg ->
                            runs { ctx ->
                                ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                                val currencyName = ctx.requireArgument(currencyArg)
                                val page = ctx.requireArgument(pageArg)
                                val currency = cachedDao.getAllCurrencies().firstOrNull {
                                    it.name.equals(
                                        currencyName,
                                        true
                                    )
                                }
                                if (currency == null) {
                                    ctx.getSender().sendMessage(translation.economy.currencyNotFound.component)
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
                            runs { ctx ->
                                ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                                val currencyName = ctx.requireArgument(currencyArg)
                                val currency = cachedDao.getAllCurrencies().firstOrNull {
                                    it.name.equals(
                                        currencyName,
                                        true
                                    )
                                }
                                if (currency == null) {
                                    ctx.getSender().sendMessage(translation.economy.currencyNotFound.component)
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
                                runs { ctx ->
                                    ctx.requirePermission(PluginPermission.SET_BALANCE)
                                    val currencyName = ctx.requireArgument(currencyArg)
                                    val currency = cachedDao.getAllCurrencies().firstOrNull {
                                        it.name.equals(
                                            currencyName,
                                            true
                                        )
                                    }
                                    if (currency == null) {
                                        ctx.getSender().sendMessage(translation.economy.currencyNotFound.component)
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
                                runs { ctx ->
                                    ctx.requirePermission(PluginPermission.SET_BALANCE)
                                    val currencyName = ctx.requireArgument(currencyArg)
                                    val currency = cachedDao.getAllCurrencies().firstOrNull {
                                        it.name.equals(
                                            currencyName,
                                            true
                                        )
                                    }
                                    if (currency == null) {
                                        ctx.getSender().sendMessage(translation.economy.currencyNotFound.component)
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
