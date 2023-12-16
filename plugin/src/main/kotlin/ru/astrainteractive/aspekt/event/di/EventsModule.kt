package ru.astrainteractive.aspekt.event.di

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.crop.AutoCropEvent
import ru.astrainteractive.aspekt.event.crop.di.AutoCropDependencies
import ru.astrainteractive.aspekt.event.discord.DiscordEvent
import ru.astrainteractive.aspekt.event.discord.di.DiscordEventDependencies
import ru.astrainteractive.aspekt.event.moneydrop.di.MoneyDropModule
import ru.astrainteractive.aspekt.event.restrictions.RestrictionsEvent
import ru.astrainteractive.aspekt.event.restrictions.di.RestrictionsDependencies
import ru.astrainteractive.aspekt.event.sit.di.SitModule
import ru.astrainteractive.aspekt.event.sort.SortEvent
import ru.astrainteractive.aspekt.event.sort.di.SortDependencies
import ru.astrainteractive.aspekt.event.tc.TCEvent
import ru.astrainteractive.aspekt.event.tc.di.TCDependencies
import ru.astrainteractive.aspekt.module.adminprivate.di.AdminPrivateModule
import ru.astrainteractive.aspekt.module.adminprivate.event.AdminPrivateEvent
import ru.astrainteractive.aspekt.module.adminprivate.event.di.AdminPrivateDependencies
import ru.astrainteractive.klibs.kdi.Module

interface EventsModule : Module {
    val tcEvent: TCEvent
    val sortEvent: SortEvent
    val sitModule: SitModule
    val restrictionsEvent: RestrictionsEvent
    val discordEvent: DiscordEvent?
    val autoCropEvent: AutoCropEvent
    val adminPrivateEvent: AdminPrivateEvent
    val moneyDropModule: MoneyDropModule

    class Default(coreModule: CoreModule, adminPrivateModule: AdminPrivateModule) : EventsModule {

        override val tcEvent: TCEvent by lazy {
            val tcDependencies: TCDependencies = TCDependencies.Default(coreModule)
            TCEvent(tcDependencies)
        }

        override val sortEvent: SortEvent by lazy {
            val sortDependencies: SortDependencies = SortDependencies.Default(coreModule)
            SortEvent(sortDependencies)
        }
        override val sitModule: SitModule by lazy {
            SitModule.Default(coreModule)
        }

        override val restrictionsEvent: RestrictionsEvent by lazy {
            val restrictionsDependencies: RestrictionsDependencies = RestrictionsDependencies.Default(coreModule)
            RestrictionsEvent(restrictionsDependencies)
        }

        override val discordEvent: DiscordEvent? by lazy {
            Bukkit.getPluginManager().getPlugin("DiscordSRV") ?: return@lazy null
            Bukkit.getPluginManager().getPlugin("LuckPerms") ?: return@lazy null
            val discordEventDependencies = DiscordEventDependencies.Default(coreModule)
            DiscordEvent(discordEventDependencies)
        }

        override val autoCropEvent: AutoCropEvent by lazy {
            val autoCropDependencies: AutoCropDependencies = AutoCropDependencies.Default(coreModule)
            AutoCropEvent(autoCropDependencies)
        }

        override val adminPrivateEvent: AdminPrivateEvent by lazy {
            val adminPrivateDependencies: AdminPrivateDependencies = AdminPrivateDependencies.Default(
                coreModule = coreModule,
                adminPrivateModule = adminPrivateModule
            )
            AdminPrivateEvent(adminPrivateDependencies)
        }
        override val moneyDropModule: MoneyDropModule by lazy {
            MoneyDropModule.Default(coreModule)
        }
    }
}
