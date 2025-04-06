package ru.astrainteractive.aspekt.module.economy.command.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.command.ekon.EkonCommandRegistry
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

internal interface EconomyCommandModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule,
        databaseModule: EconomyDatabaseModule,
        bukkitCoreModule: BukkitCoreModule
    ) : EconomyCommandModule {
        private val econCommandRegistry = EkonCommandRegistry(
            plugin = bukkitCoreModule.plugin,
            getTranslation = { coreModule.translation.cachedValue },
            getKyori = { coreModule.kyoriComponentSerializer.cachedValue },
            dao = databaseModule.economyDao,
            cachedDao = databaseModule.cachedDao
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = { econCommandRegistry.register() }
        )
    }
}
