package ru.astrainteractive.aspekt.module.claims.command.discordlink.di

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.discordlink.controller.AddMoneyController
import ru.astrainteractive.aspekt.module.claims.command.discordlink.controller.DiscordController
import ru.astrainteractive.aspekt.module.claims.command.discordlink.controller.LuckPermsController
import ru.astrainteractive.aspekt.module.claims.command.discordlink.controller.RoleController
import ru.astrainteractive.aspekt.module.claims.command.discordlink.controller.di.RoleControllerDependencies
import ru.astrainteractive.aspekt.module.claims.command.discordlink.event.DiscordEvent
import ru.astrainteractive.aspekt.module.claims.command.discordlink.job.DiscordLinkJob
import ru.astrainteractive.aspekt.module.claims.command.discordlink.job.di.DiscordLinkJobDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import java.io.File

interface DiscordLinkModule {
    val lifecycle: Lifecycle

    val tempFile: File
    val tempFileConfiguration: Krate<FileConfiguration>
    val discordController: RoleController.Discord

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule
    ) : DiscordLinkModule {

        private val roleControllerDependencies by lazy {
            RoleControllerDependencies.Default(
                coreModule = coreModule,
                bukkitCoreModule = bukkitCoreModule,
                discordLinkModule = this
            )
        }

        private val luckPermsController: RoleController.Minecraft by lazy {
            LuckPermsController(roleControllerDependencies)
        }

        private val addMoneyController: RoleController by lazy {
            AddMoneyController(roleControllerDependencies)
        }

        override val discordController: RoleController.Discord by lazy {
            DiscordController(roleControllerDependencies)
        }

        override val tempFile = coreModule.dataFolder.resolve("temp.yml")

        override val tempFileConfiguration: Krate<FileConfiguration> = DefaultMutableKrate(
            factory = { YamlConfiguration() },
            loader = { YamlConfiguration.loadConfiguration(tempFile) }
        )

        private val discordEvent: DiscordEvent? by lazy {
            Bukkit.getPluginManager().getPlugin("DiscordSRV") ?: return@lazy null
            Bukkit.getPluginManager().getPlugin("LuckPerms") ?: return@lazy null
            DiscordEvent(
                dependencies = DiscordEventDependencies.Default(
                    coreModule = coreModule,
                    discordController = discordController,
                    luckPermsController = luckPermsController,
                    addMoneyController = addMoneyController
                )
            )
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
                    discordEvent?.onEnable(bukkitCoreModule.plugin)
                    discordLinkJob?.onEnable()
                },
                onDisable = {
                    discordEvent?.onDisable()
                    discordLinkJob?.onDisable()
                },
                onReload = {
                    tempFileConfiguration.loadAndGet()
                }
            )
        }
    }
}
