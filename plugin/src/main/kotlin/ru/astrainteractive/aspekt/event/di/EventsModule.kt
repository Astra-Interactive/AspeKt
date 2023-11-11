package ru.astrainteractive.aspekt.event.di

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.adminprivate.AdminPrivateEvent
import ru.astrainteractive.aspekt.event.adminprivate.di.AdminPrivateDependencies
import ru.astrainteractive.aspekt.event.crop.AutoCropEvent
import ru.astrainteractive.aspekt.event.crop.di.AutoCropDependencies
import ru.astrainteractive.aspekt.event.discord.DiscordEvent
import ru.astrainteractive.aspekt.event.discord.di.DiscordEventDependencies
import ru.astrainteractive.aspekt.event.restrictions.RestrictionsEvent
import ru.astrainteractive.aspekt.event.restrictions.di.RestrictionsDependencies
import ru.astrainteractive.aspekt.event.sit.di.SitModule
import ru.astrainteractive.aspekt.event.sort.SortEvent
import ru.astrainteractive.aspekt.event.sort.di.SortDependencies
import ru.astrainteractive.aspekt.event.tc.TCEvent
import ru.astrainteractive.aspekt.event.tc.di.TCDependencies
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.getValue

interface EventsModule : Module {
    val tcEvent: TCEvent
    val sortEvent: SortEvent
    val sitModule: SitModule
    val restrictionsEvent: RestrictionsEvent
    val discordEvent: DiscordEvent?
    val autoCropEvent: AutoCropEvent
    val adminPrivateEvent: AdminPrivateEvent

    class Default(rootModule: RootModule) : EventsModule {

        override val tcEvent: TCEvent by lazy {
            val tcDependencies: TCDependencies = TCDependencies.Default(rootModule)
            TCEvent(tcDependencies)
        }

        override val sortEvent: SortEvent by lazy {
            val sortDependencies: SortDependencies = SortDependencies.Default(rootModule)
            SortEvent(sortDependencies)
        }
        override val sitModule: SitModule by lazy {
            SitModule.Default(rootModule)
        }

        override val restrictionsEvent: RestrictionsEvent by lazy {
            val restrictionsDependencies: RestrictionsDependencies = RestrictionsDependencies.Default(rootModule)
            RestrictionsEvent(restrictionsDependencies)
        }

        override val discordEvent: DiscordEvent? by lazy {
            Bukkit.getPluginManager().getPlugin("DiscordSRV") ?: return@lazy null
            Bukkit.getPluginManager().getPlugin("LuckPerms") ?: return@lazy null
            val discordEventDependencies = DiscordEventDependencies.Default(rootModule)
            DiscordEvent(discordEventDependencies)
        }

        override val autoCropEvent: AutoCropEvent by lazy {
            val autoCropDependencies: AutoCropDependencies = AutoCropDependencies.Default(rootModule)
            AutoCropEvent(autoCropDependencies)
        }

        override val adminPrivateEvent: AdminPrivateEvent by lazy {
            val adminPrivateDependencies: AdminPrivateDependencies = AdminPrivateDependencies.Default(
                rootModule = rootModule,
                adminPrivateController = rootModule.adminPrivateModule.adminPrivateController
            )
            AdminPrivateEvent(adminPrivateDependencies)
        }
    }
}
