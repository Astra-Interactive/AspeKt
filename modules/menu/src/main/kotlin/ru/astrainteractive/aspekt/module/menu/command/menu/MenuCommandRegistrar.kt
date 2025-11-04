package ru.astrainteractive.aspekt.module.menu.command.menu

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.aspekt.module.menu.router.MenuRouter
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.util.argument
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.hints
import ru.astrainteractive.astralibs.command.api.util.requireArgument
import ru.astrainteractive.astralibs.command.api.util.requirePlayer
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

/**
 * Menu command registrar. Builds Brigadier node for:
 * /menu [menu]
 */
internal class MenuCommandRegistrar(
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val menuRouter: () -> MenuRouter,
    private val menuModels: List<MenuModel>
) {
    private val translation by translationKrate
    private val kyori by kyoriKrate

    fun createNode(): LiteralCommandNode<CommandSourceStack> {
        return command("menu") {
            runs { ctx ->
                val player: Player = ctx.requirePlayer()
                val menuModel = menuModels.firstOrNull()
                if (menuModel == null) {
                    translation.general.menuNotFound
                        .let(kyori::toComponent)
                        .run(ctx.source.sender::sendMessage)
                } else {
                    menuRouter.invoke().openMenu(player = player, menuModel = menuModel)
                }
            }
            argument("menu", StringArgumentType.string()) { menuArg ->
                hints { menuModels.map(MenuModel::command) }
                runs { ctx ->
                    val player: Player = ctx.requirePlayer()
                    val cmd = ctx.requireArgument(menuArg)
                    val menuModel = menuModels.firstOrNull { it.command == cmd } ?: menuModels.firstOrNull()
                    if (menuModel == null) {
                        translation.general.menuNotFound
                            .let(kyori::toComponent)
                            .run(ctx.source.sender::sendMessage)
                    } else {
                        menuRouter.invoke().openMenu(player = player, menuModel = menuModel)
                    }
                }
            }
        }.build()
    }
}
