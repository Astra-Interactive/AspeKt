package ru.astrainteractive.aspekt.module.economy.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.command.ekon.EkonCommandExecutor
import ru.astrainteractive.aspekt.module.economy.command.ekon.EkonLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

internal class EconomyCommandModule(
    private val coreModule: CoreModule,
    private val databaseModule: EconomyDatabaseModule,
    private val bukkitCoreModule: BukkitCoreModule
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

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
            bukkitCoreModule.commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
