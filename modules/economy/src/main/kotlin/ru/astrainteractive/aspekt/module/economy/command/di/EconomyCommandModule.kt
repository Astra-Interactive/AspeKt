package ru.astrainteractive.aspekt.module.economy.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.command.EkonCommandExecutor
import ru.astrainteractive.aspekt.module.economy.command.EkonLiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

internal class EconomyCommandModule(
    coreModule: CoreModule,
    databaseModule: EconomyDatabaseModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val executor = EkonCommandExecutor(
        translationKrate = coreModule.translationKrate,
        dispatchers = coreModule.dispatchers,
        repository = databaseModule.economyRepository
    )

    private val nodes = buildList {
        EkonLiteralArgumentBuilder(
            currencyCache = databaseModule.currencyCache,
            executor = executor,
            translationKrate = coreModule.translationKrate,
            multiplatformCommand = coreModule.multiplatformCommand,
            platformServer = coreModule.platformServer,
            commandExceptionHandler = coreModule.commandExceptionHandler
        ).create().run(::add)
    }

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            bukkitCoreModule.commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
