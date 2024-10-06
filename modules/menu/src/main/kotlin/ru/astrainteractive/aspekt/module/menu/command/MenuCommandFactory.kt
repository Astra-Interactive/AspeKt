package ru.astrainteractive.aspekt.module.menu.command

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.aspekt.module.menu.router.MenuRouter
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.util.StringListExt.withEntry
import ru.astrainteractive.klibs.kstorage.api.Krate

internal class MenuCommandFactory(
    private val plugin: JavaPlugin,
    private val menuRouterProvider: () -> MenuRouter,
    private val kyoriComponentSerializer: Krate<KyoriComponentSerializer>,
    menuModelProvider: Krate<List<MenuModel>>,
    translationProvider: Krate<PluginTranslation>,
) {
    private val menuModels by menuModelProvider
    private val translation by translationProvider

    private fun menuCompleter() = plugin.getCommand("menu")?.setTabCompleter { sender, command, label, args ->
        when {
            args.size <= 1 -> menuModels.map { it.command }.withEntry(args.getOrNull(0))
            else -> emptyList()
        }
    }

    private fun menu() = plugin.getCommand("menu")?.setExecutor { sender, command, label, args ->
        val command = args.getOrNull(0).orEmpty()
        val menuModel = menuModels.firstOrNull { it.command == command }
        if (menuModel == null) {
            kyoriComponentSerializer.cachedValue.toComponent(translation.general.menuNotFound)
                .run(sender::sendMessage)
            return@setExecutor true
        }
        menuRouterProvider.invoke().openMenu(
            player = sender as Player,
            menuModel = menuModel
        )
        true
    }

    private fun invClose() = plugin.getCommand("invclose")?.setExecutor { sender, command, label, args ->
        args.getOrNull(0)?.let(Bukkit::getPlayer)?.closeInventory()
        true
    }

    fun create() {
        menuCompleter()
        menu()
        invClose()
    }
}
