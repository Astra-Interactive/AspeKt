package ru.astrainteractive.aspekt.module.menu.command

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.aspekt.module.menu.router.MenuRouter
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.command.registerTabCompleter
import ru.astrainteractive.astralibs.command.types.PrimitiveArgumentType
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astralibs.util.withEntry
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

internal class MenuCommandFactory(
    private val plugin: JavaPlugin,
    private val translationContext: BukkitTranslationContext,
    private val menuRouter: Provider<MenuRouter>,
    menuModelProvider: Provider<List<MenuModel>>,
    translationProvider: Provider<PluginTranslation>,
) : Factory<Unit> {
    private val menuModels by menuModelProvider
    private val translation by translationProvider

    private fun menuCompleter() = plugin.registerTabCompleter("menu") {
        when {
            args.size <= 1 -> menuModels.map { it.command }.withEntry(args.getOrNull(0))
            else -> emptyList()
        }
    }

    private fun menu() = plugin.registerCommand("menu") {
        val command = argument(0, PrimitiveArgumentType.String).resultOrNull() ?: return@registerCommand
        val menuModel = menuModels.firstOrNull { it.command == command }
        if (menuModel == null) {
            with(translationContext) {
                sender.sendMessage(translation.general.menuNotFound)
                return@registerCommand
            }
        }
        menuRouter.provide().openMenu(
            player = sender as Player,
            menuModel = menuModel
        )
    }

    private fun invClose() = plugin.registerCommand("invclose") {
        args.getOrNull(0)?.let(Bukkit::getPlayer)?.closeInventory()
    }

    override fun create() {
        menuCompleter()
        menu()
        invClose()
    }
}
