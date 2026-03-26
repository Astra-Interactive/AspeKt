package ru.astrainteractive.aspekt.module.economy.command.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.command.ekon.EkonCommandExecutor
import ru.astrainteractive.aspekt.module.economy.command.ekon.EkonLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

internal class EconomyCommandModule(
    private val coreModule: CoreModule,
    private val databaseModule: EconomyDatabaseModule,
    private val bukkitCoreModule: BukkitCoreModule
) {
    private val executor = EkonCommandExecutor(
        kyoriKrate = coreModule.kyoriKrate,
        translationKrate = coreModule.translationKrate,
        dao = databaseModule.economyDao
    )

    private val nodes = buildList {
        EkonLiteralArgumentBuilder(
            cachedDao = databaseModule.cachedDao,
            executor = executor,
            translationKrate = coreModule.translationKrate,
            kyoriKrate = coreModule.kyoriKrate,
            multiplatformCommand = coreModule.multiplatformCommand,
            platformServer = coreModule.platformServer
        ).create().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(bukkitCoreModule.commandRegistrarContext::registerWhenReady)
        }
    )
}
