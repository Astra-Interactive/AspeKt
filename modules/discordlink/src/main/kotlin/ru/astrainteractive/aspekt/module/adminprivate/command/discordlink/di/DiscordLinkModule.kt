package ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.di

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.AddMoneyController
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.DiscordController
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.LuckPermsController
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.RoleController
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.controller.di.RoleControllerDependencies
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.event.DiscordEvent
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.job.DiscordLinkJob
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.job.di.DiscordLinkJobDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kdi.Reloadable
import java.io.File

interface DiscordLinkModule {
    val lifecycle: Lifecycle

    val tempFile: File
    val tempFileConfiguration: Reloadable<FileConfiguration>
    val discordController: RoleController.Discord

    class Default(coreModule: CoreModule) : DiscordLinkModule {
        private val luckPermsController: RoleController.Minecraft by lazy {
            LuckPermsController(roleControllerDependencies)
        }

        private val addMoneyController: RoleController by lazy {
            AddMoneyController(roleControllerDependencies)
        }

        private val roleControllerDependencies by lazy {
            RoleControllerDependencies.Default(
                coreModule,
                this
            )
        }

        override val discordController: RoleController.Discord by lazy {
            DiscordController(roleControllerDependencies)
        }

        private val dependencies by lazy {
            DiscordEventDependencies.Default(
                coreModule = coreModule,
                discordController = discordController,
                luckPermsController = luckPermsController,
                addMoneyController = addMoneyController
            )
        }

        override val tempFile by lazy {
            coreModule.plugin.value.dataFolder.resolve("temp.yml")
        }

        override val tempFileConfiguration: Reloadable<FileConfiguration> = Reloadable {
            YamlConfiguration.loadConfiguration(tempFile)
        }

        private val discordEvent: DiscordEvent? by lazy {
            Bukkit.getPluginManager().getPlugin("DiscordSRV") ?: return@lazy null
            Bukkit.getPluginManager().getPlugin("LuckPerms") ?: return@lazy null
            DiscordEvent(dependencies)
        }

        private val discordLinkJob by lazy {
            Bukkit.getPluginManager().getPlugin("DiscordSRV") ?: return@lazy null
            Bukkit.getPluginManager().getPlugin("LuckPerms") ?: return@lazy null
            DiscordLinkJob(
                dependencies = DiscordLinkJobDependencies.Default(
                    coreModule = coreModule,
                    luckPermsRoleController = luckPermsController,
                    discordRoleController = discordController
                )
            )
        }

        override val lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    discordEvent?.onEnable()
                    discordLinkJob?.onEnable()
                },
                onDisable = {
                    discordEvent?.onDisable()
                    discordLinkJob?.onDisable()
                },
                onReload = {
                    discordLinkJob?.onDisable()
                    discordLinkJob?.onEnable()
                    tempFileConfiguration.reload()
                }
            )
        }
    }
}
