package ru.astrainteractive.aspekt.module.economy.di

import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface EconomyModule {
    val lifecycle: Lifecycle

    class Default : EconomyModule {
        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {},
            onReload = {},
            onDisable = {}
        )
    }
}
