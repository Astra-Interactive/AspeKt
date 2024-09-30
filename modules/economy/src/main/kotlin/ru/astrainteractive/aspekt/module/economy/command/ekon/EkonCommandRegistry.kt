package ru.astrainteractive.aspekt.module.economy.command.ekon

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.economy.command.ekon.argument.CurrencyArgument
import ru.astrainteractive.aspekt.module.economy.command.ekon.argument.OfflinePlayerArgument
import ru.astrainteractive.aspekt.module.economy.database.dao.CachedDao
import ru.astrainteractive.aspekt.module.economy.database.dao.EconomyDao
import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.exception.DefaultCommandException
import ru.astrainteractive.astralibs.command.api.util.PluginExt.registerCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.util.StringListExt.withEntry

internal class EkonCommandRegistry(
    private val plugin: JavaPlugin,
    private val getKyori: () -> KyoriComponentSerializer,
    private val getTranslation: () -> PluginTranslation,
    private val dao: EconomyDao,
    private val cachedDao: CachedDao
) : Logger by JUtiltLogger("EkonCommandRegistry") {
    private val kyori get() = getKyori.invoke()
    private val translation get() = getTranslation.invoke()

    @Suppress("CyclomaticComplexMethod")
    private fun adminPrivateCompleter() =
        plugin.getCommand(EkonCommand.ALIAS)?.setTabCompleter { sender, command, label, args ->
            when {
                args.size <= 1 -> listOf("list", "top", "balance", "set", "add").withEntry(args.getOrNull(0))

                else -> {
                    when (args.getOrNull(0)) {
                        "list" -> emptyList()
                        "top" -> {
                            when (args.size) {
                                2 -> cachedDao.getAllCurrencies()
                                    .map(CurrencyModel::name)
                                    .withEntry(args.getOrNull(1))

                                else -> emptyList()
                            }
                        }

                        "balance" -> {
                            when (args.size) {
                                2 -> cachedDao.getAllCurrencies()
                                    .map(CurrencyModel::name)
                                    .withEntry(args.getOrNull(1))

                                3 -> Bukkit.getOnlinePlayers().map(Player::getName).withEntry(args.getOrNull(2))

                                else -> emptyList()
                            }
                        }

                        "set", "add" -> {
                            when (args.size) {
                                2 -> cachedDao.getAllCurrencies()
                                    .map(CurrencyModel::name)
                                    .withEntry(args.getOrNull(1))

                                3 -> Bukkit.getOnlinePlayers().map(Player::getName).withEntry(args.getOrNull(2))

                                4 -> listOf("123")

                                else -> emptyList()
                            }
                        }

                        else -> emptyList()
                    }
                }
            }
        }

    fun register() {
        adminPrivateCompleter()
        plugin.registerCommand(
            alias = EkonCommand.ALIAS,
            commandParser = EkonCommandParser(
                cachedDao = cachedDao
            ),
            commandExecutor = EkonCommandExecutor(
                getKyori = getKyori,
                getTranslation = getTranslation,
                dao = dao
            ),
            errorHandler = { context, throwable ->
                when (throwable) {
                    is DefaultCommandException -> with(kyori) {
                        when (throwable) {
                            is DefaultCommandException.ArgumentTypeException -> {
                                context.sender.sendMessage(translation.general.wrongUsage.component)
                            }

                            is DefaultCommandException.BadArgumentException -> {
                                context.sender.sendMessage(translation.general.wrongUsage.component)
                            }

                            is DefaultCommandException.NoPermissionException -> {
                                context.sender.sendMessage(translation.general.noPermission.component)
                            }
                        }
                    }

                    is CurrencyArgument.CurrencyNotFoundException -> with(kyori) {
                        context.sender.sendMessage(translation.economy.currencyNotFound.component)
                    }

                    is OfflinePlayerArgument.PlayerNotFound -> with(kyori) {
                        context.sender.sendMessage(translation.economy.playerNotFound.component)
                    }

                    else -> with(kyori) {
                        error { "#errorHandler handler for ${throwable::class} not found" }
                        context.sender.sendMessage(translation.general.noPermission.component)
                    }
                }
            }
        )
    }
}