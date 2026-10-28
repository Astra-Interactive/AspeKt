package ru.astrainteractive.aspekt.module.menu.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.menu.command.di.MenuCommandModule
import ru.astrainteractive.aspekt.module.menu.file.readMenuModels
import ru.astrainteractive.aspekt.module.menu.router.BukkitMenuRouter
import ru.astrainteractive.aspekt.module.menu.router.MenuRouter
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.asCachedKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import java.io.File

class MenuModule(
    private val coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule
) {
    private val menuModels = DefaultMutableKrate(
        loader = {
            readMenuModels(
                dataFolder = bukkitCoreModule.plugin.dataFolder,
                yamlFormat = coreModule.yamlFormat
            )
        },
        factory = { emptyList() }
    ).asCachedKrate()

    private val menuRouter: MenuRouter = BukkitMenuRouter(coreModule, bukkitCoreModule)

    private val menuCommandModule = MenuCommandModule(
        coreModule = coreModule,
        bukkitCoreModule = bukkitCoreModule,
        menuRouter = { menuRouter },
        menuModelsKrate = menuModels
    )

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                menuCommandModule.lifecycle.onEnable()
            },
            onReload = {
                menuModels.getValue()
            },
            onDisable = {
                menuCommandModule.lifecycle.onDisable()
            }
        )
    }

    companion object {
        fun resolveConfigurationFile(dataFolder: File): File = dataFolder.resolve("menu.yml")
    }
}
