package ru.astrainteractive.aspekt.module.economy.command.ekon

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.economy.database.dao.CachedDao
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.argumenttype.OfflinePlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.util.argument
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.hints
import ru.astrainteractive.astralibs.command.api.util.literal
import ru.astrainteractive.astralibs.command.api.util.requireArgument
import ru.astrainteractive.astralibs.command.api.util.requirePermission
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
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
internal class EkonCommandRegistrar(
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val cachedDao: CachedDao,
    private val executor: EkonCommandExecutor,
) {
    private val translation by translationKrate
    private val kyori by kyoriKrate
    private fun currencyNames(): List<String> = cachedDao.getAllCurrencies().map { it.name }

    fun createNode(): LiteralCommandNode<CommandSourceStack> {
        return command("ekon") {
            runs { ctx ->
                ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                val message = translation.general.wrongUsage
                ctx.source.sender.sendMessage(kyori.toComponent(message))
            }
            literal("list") {
                runs { ctx ->
                    ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                    EkonCommand.Model.ListCurrencies(ctx.source.sender)
                        .run(executor::execute)
                }
            }
            literal("top") {
                argument("currency", StringArgumentType.string()) { currencyArg ->
                    hints { currencyNames() }
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                        val currencyName = ctx.requireArgument(currencyArg)
                        val currency = cachedDao.getAllCurrencies().firstOrNull { it.name.equals(currencyName, true) }
                        if (currency == null) {
                            ctx.source.sender.sendMessage(kyori.toComponent(translation.economy.currencyNotFound))
                            return@runs
                        }
                        EkonCommand.Model.Top(
                            sender = ctx.source.sender,
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
                                ctx.source.sender.sendMessage(kyori.toComponent(translation.economy.currencyNotFound))
                                return@runs
                            }
                            EkonCommand.Model.Top(
                                sender = ctx.source.sender,
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
                                ctx.source.sender.sendMessage(kyori.toComponent(translation.economy.currencyNotFound))
                                return@runs
                            }
                            val offlinePlayer: OfflinePlayer = ctx.requireArgument(
                                playerArg,
                                OfflinePlayerArgumentConverter
                            )
                            EkonCommand.Model.Balance(
                                sender = ctx.source.sender,
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
                                    ctx.source.sender.sendMessage(
                                        kyori.toComponent(translation.economy.currencyNotFound)
                                    )
                                    return@runs
                                }
                                val offlinePlayer: OfflinePlayer = ctx.requireArgument(
                                    playerArg,
                                    OfflinePlayerArgumentConverter
                                )
                                val amount: Double = ctx.requireArgument(amountArg)
                                EkonCommand.Model.Set(
                                    sender = ctx.source.sender,
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
                                    ctx.source.sender.sendMessage(
                                        kyori.toComponent(translation.economy.currencyNotFound)
                                    )
                                    return@runs
                                }
                                val offlinePlayer: OfflinePlayer = ctx.requireArgument(
                                    playerArg,
                                    OfflinePlayerArgumentConverter
                                )
                                val amount: Double = ctx.requireArgument(amountArg)
                                EkonCommand.Model.Add(
                                    sender = ctx.source.sender,
                                    otherPlayer = offlinePlayer,
                                    currency = currency,
                                    amount = amount
                                ).run(executor::execute)
                            }
                        }
                    }
                }
            }
        }.build()
    }
}
