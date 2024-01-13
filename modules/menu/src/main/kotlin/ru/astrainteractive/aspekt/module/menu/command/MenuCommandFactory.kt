package ru.astrainteractive.aspekt.module.menu.command

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.aspekt.module.menu.router.MenuRouter
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.type.PrimitiveArgumentType
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.util.StringListExt.withEntry
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

internal class MenuCommandFactory(
    private val plugin: JavaPlugin,
    private val menuRouter: Provider<MenuRouter>,
    private val kyoriComponentSerializer: Dependency<KyoriComponentSerializer>,
    menuModelProvider: Provider<List<MenuModel>>,
    translationProvider: Provider<PluginTranslation>,
) : Factory<Unit> {
    private val menuModels by menuModelProvider
    private val translation by translationProvider

    private fun menuCompleter() = plugin.getCommand("menu")?.setTabCompleter { sender, command, label, args ->
        when {
            args.size <= 1 -> menuModels.map { it.command }.withEntry(args.getOrNull(0))
            else -> emptyList()
        }
    }

    private fun menu() = plugin.getCommand("menu")?.setExecutor { sender, command, label, args ->

        val command = PrimitiveArgumentType.String.transform(args.getOrNull(0)) ?: return@setExecutor true
        val menuModel = menuModels.firstOrNull { it.command == command }
        if (menuModel == null) {
            kyoriComponentSerializer.value.toComponent(translation.general.menuNotFound)
                .run(sender::sendMessage)
            return@setExecutor true
        }
        menuRouter.provide().openMenu(
            player = sender as Player,
            menuModel = menuModel
        )
        true
    }

    private fun invClose() = plugin.getCommand("invclose")?.setExecutor { sender, command, label, args ->
        args.getOrNull(0)?.let(Bukkit::getPlayer)?.closeInventory()
        true
    }

    override fun create() {
        menuCompleter()
        menu()
        invClose()
    }
}
