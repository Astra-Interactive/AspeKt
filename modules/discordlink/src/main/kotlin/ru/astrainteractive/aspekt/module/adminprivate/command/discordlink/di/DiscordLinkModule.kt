package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.di

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.AddMoneyController
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.DiscordController
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.LuckPermsController
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.RoleController
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.di.RoleControllerDependencies
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.event.DiscordEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kdi.Factory

interface DiscordLinkModule {
    val discordLinkLifecycleFactory: Factory<Lifecycle>

    class Default(coreModule: CoreModule) : DiscordLinkModule {

        private val dependencies by lazy {
            val roleControllerDependencies by lazy {
                RoleControllerDependencies.Default(coreModule)
            }
            val luckPermsController: RoleController by lazy {
                LuckPermsController(roleControllerDependencies)
            }
            val discordController: RoleController by lazy {
                DiscordController(roleControllerDependencies)
            }
            val addMoneyController: RoleController by lazy {
                AddMoneyController(roleControllerDependencies)
            }
            DiscordEventDependencies.Default(
                coreModule = coreModule,
                discordController = discordController,
                luckPermsController = luckPermsController,
                addMoneyController = addMoneyController
            )
        }

        private val discordEvent: DiscordEvent? by lazy {
            Bukkit.getPluginManager().getPlugin("DiscordSRV") ?: return@lazy null
            Bukkit.getPluginManager().getPlugin("LuckPerms") ?: return@lazy null
            DiscordEvent(dependencies)
        }
        override val discordLinkLifecycleFactory: Factory<Lifecycle> = Factory {
            Lifecycle.Lambda(
                onEnable = {
                    discordEvent?.onEnable()
                },
                onDisable = {
                    discordEvent?.onDisable()
                }
            )
        }
    }
}
