package ru.astrainteractive.aspekt.module.menu.command.menu

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.aspekt.module.menu.router.MenuRouter
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

/**
 * Menu command registrar. Builds Brigadier node for:
 * /menu [menu]
 */
internal class MenuLiteralArgumentBuilder(
    translationKrate: CachedKrate<PluginTranslation>,
    private val menuRouter: () -> MenuRouter,
    menuModelsKrate: CachedKrate<List<MenuModel>>,
    private val multiplatformCommand: MultiplatformCommand
) {
    private val translation by translationKrate
    private val menuModels by menuModelsKrate

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("menu") {
                runs { ctx ->
                    val player = ctx.requirePlayer()
                    val menuModel = menuModels.firstOrNull()
                    if (menuModel == null) {
                        ctx.getSender().sendMessage(translation.menu.notFound)
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
                            ctx.getSender().sendMessage(
                                translation.menu.notFound
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
