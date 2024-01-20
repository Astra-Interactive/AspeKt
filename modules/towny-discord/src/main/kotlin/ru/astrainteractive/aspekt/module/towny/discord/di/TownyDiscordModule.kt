package ru.astrainteractive.aspekt.module.towny.discord.di

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.job.ScheduledJob
import ru.astrainteractive.aspekt.module.towny.discord.job.TownyDiscordRoleJob
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface TownyDiscordModule {
    val lifecycle: Lifecycle

    class Default(coreModule: CoreModule) : TownyDiscordModule {
        private val job: ScheduledJob? by lazy {
            if (Bukkit.getPluginManager().getPlugin("Towny") == null) return@lazy null
            TownyDiscordRoleJob(
                scope = coreModule.scope.value,
                dispatchers = coreModule.dispatchers.value,
                configurationDependency = coreModule.pluginConfig
            )
        }
        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    job?.onEnable()
                },
                onDisable = {
                    job?.onDisable()
                },
                onReload = {
                    job?.onDisable()
                    job?.onEnable()
                }

            )
        }
    }
}
