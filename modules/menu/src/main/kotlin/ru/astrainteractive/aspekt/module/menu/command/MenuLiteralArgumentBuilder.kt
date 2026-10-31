package ru.astrainteractive.aspekt.module.menu.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.aspekt.module.menu.model.MenuConfiguration
import ru.astrainteractive.aspekt.module.menu.router.api.MenuRouter
import ru.astrainteractive.aspekt.plugin.model.PluginTranslation
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

internal class MenuLiteralArgumentBuilder(
    translationKrate: CachedKrate<PluginTranslation>,
    private val menuRouter: () -> MenuRouter,
    menuModelsKrate: CachedKrate<List<MenuConfiguration>>,
    private val multiplatformCommand: MultiplatformCommand,
    private val commandExceptionHandler: CommandExceptionHandler
) {
    private val translation by translationKrate
    private val menuModels by menuModelsKrate

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("menu") {
                runs(commandExceptionHandler::onException) { ctx ->
                    val player = ctx.requirePlayer()
                    val menuModel = menuModels.firstOrNull()
                    if (menuModel == null) {
                        ctx.getSender().sendMessage(translation.menu.notFound)
                    } else {
                        menuRouter.invoke().openMenu(player = player, menuModel = menuModel)
                    }
                }
                argument(
                    "menu",
                    StringArgumentType.string()
                ) { menuArg ->
                    hints { menuModels.map(MenuConfiguration::command) }
                    runs(commandExceptionHandler::onException) { ctx ->
                        val player = ctx.requirePlayer()
                        val cmd = ctx.requireArgument(menuArg)
                        val menuModel = menuModels.firstOrNull { menu -> menu.command == cmd }
                            ?: menuModels.firstOrNull()
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
