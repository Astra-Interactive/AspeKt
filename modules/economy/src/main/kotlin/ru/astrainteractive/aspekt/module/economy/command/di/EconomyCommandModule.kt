package ru.astrainteractive.aspekt.module.economy.command.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.economy.command.ekon.EkonCommandRegistry
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

internal interface EconomyCommandModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule,
        databaseModule: EconomyDatabaseModule
    ) : EconomyCommandModule {
        private val econCommandRegistry = EkonCommandRegistry(
            plugin = coreModule.plugin.value,
            getTranslation = { coreModule.translation.value },
            getKyori = { coreModule.kyoriComponentSerializer.value },
            dao = databaseModule.economyDao,
            cachedDao = databaseModule.cachedDao
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = { econCommandRegistry.register() }
        )
    }
}
