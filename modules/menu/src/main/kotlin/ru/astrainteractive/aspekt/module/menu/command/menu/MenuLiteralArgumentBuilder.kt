package ru.astrainteractive.aspekt.module.menu.command.menu

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.aspekt.module.menu.router.MenuRouter
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.KAudience
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.util.tryCast

/**
 * Menu command registrar. Builds Brigadier node for:
 * /menu [menu]
 */
internal class MenuLiteralArgumentBuilder(
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val menuRouter: () -> MenuRouter,
    private val menuModels: List<MenuModel>,
    private val multiplatformCommand: MultiplatformCommand
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("menu") {
                runs { ctx ->
                    val player = ctx.requirePlayer()
                    val menuModel = menuModels.firstOrNull()
                    if (menuModel == null) {
                        ctx.getSender().tryCast<KAudience>()?.sendMessage(translation.general.menuNotFound.component)
                    } else {
                        menuRouter.invoke().openMenu(player = player, menuModel = menuModel)
                    }
                }
                argument("menu", StringArgumentType.string()) { menuArg ->
                    hints { menuModels.map(MenuModel::command) }
                    runs { ctx ->
                        val player = ctx.requirePlayer()
                        val cmd = ctx.requireArgument(menuArg)
                        val menuModel = menuModels.firstOrNull { it.command == cmd } ?: menuModels.firstOrNull()
                        if (menuModel == null) {
                            ctx.getSender().tryCast<KAudience>()?.sendMessage(
                                translation.general.menuNotFound.component
                            )
                        } else {
                            menuRouter.invoke().openMenu(player = player, menuModel = menuModel)
                        }
                    }
                }
            }
        }
    }
}
