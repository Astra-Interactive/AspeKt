package ru.astrainteractive.aspekt.di

import ru.astrainteractive.aspekt.command.di.CommandManagerModule
import ru.astrainteractive.aspekt.inventorysort.di.InventorySortModule
import ru.astrainteractive.aspekt.module.adminprivate.command.discordlink.di.DiscordLinkModule
import ru.astrainteractive.aspekt.module.adminprivate.di.AdminPrivateModule
import ru.astrainteractive.aspekt.module.antiswear.di.AntiSwearModule
import ru.astrainteractive.aspekt.module.autobroadcast.di.AutoBroadcastModule
import ru.astrainteractive.aspekt.module.autocrop.di.AutoCropModule
import ru.astrainteractive.aspekt.module.chatgame.di.ChatGameModule
import ru.astrainteractive.aspekt.module.economy.di.EconomyModule
import ru.astrainteractive.aspekt.module.entities.di.EntitiesModule
import ru.astrainteractive.aspekt.module.jail.di.JailModule
import ru.astrainteractive.aspekt.module.menu.di.MenuModule
import ru.astrainteractive.aspekt.module.moneyadvancement.di.MoneyAdvancementModule
import ru.astrainteractive.aspekt.module.moneydrop.di.MoneyDropModule
import ru.astrainteractive.aspekt.module.newbee.di.NewBeeModule
import ru.astrainteractive.aspekt.module.restrictions.di.RestrictionModule
import ru.astrainteractive.aspekt.module.sit.di.SitModule
import ru.astrainteractive.aspekt.module.towny.discord.di.TownyDiscordModule
import ru.astrainteractive.aspekt.module.treecapitator.di.TreeCapitatorModule
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin

class RootModule(plugin: LifecyclePlugin) {
    val coreModule: CoreModule by lazy {
        CoreModule.Default(
            dataFolder = plugin.dataFolder,
            dispatchers = DefaultBukkitDispatchers(plugin)
        )
    }
    val bukkitCoreModule: BukkitCoreModule by lazy {
        BukkitCoreModule(plugin)
    }
    val adminPrivateModule: AdminPrivateModule by lazy {
        AdminPrivateModule.Default(coreModule, bukkitCoreModule)
    }
    val menuModule: MenuModule by lazy {
        MenuModule.Default(coreModule, bukkitCoreModule)
    }
    val sitModule: SitModule by lazy {
        SitModule.Default(coreModule, bukkitCoreModule)
    }
    val entitiesModule: EntitiesModule by lazy {
        EntitiesModule(coreModule, bukkitCoreModule)
    }
    val autoBroadcastModule by lazy {
        AutoBroadcastModule.Default(coreModule)
    }
    val discordLinkModule: DiscordLinkModule by lazy {
        DiscordLinkModule.Default(coreModule, bukkitCoreModule)
    }
    val commandManagerModule: CommandManagerModule by lazy {
        CommandManagerModule.Default(coreModule, bukkitCoreModule)
    }
    val townyDiscordModule: TownyDiscordModule by lazy {
        TownyDiscordModule.Default(coreModule, discordLinkModule)
    }
    val moneyDropModule: MoneyDropModule by lazy {
        MoneyDropModule.Default(coreModule, bukkitCoreModule)
    }
    val autoCropModule: AutoCropModule by lazy {
        AutoCropModule.Default(coreModule, bukkitCoreModule)
    }
    val newBeeModule: NewBeeModule by lazy {
        NewBeeModule.Default(coreModule, bukkitCoreModule)
    }
    val antiSwearModule: AntiSwearModule by lazy {
        AntiSwearModule.Default(coreModule, bukkitCoreModule)
    }
    val moneyAdvancementModule: MoneyAdvancementModule by lazy {
        MoneyAdvancementModule.Default(coreModule, bukkitCoreModule)
    }
    val chatGameModule: ChatGameModule by lazy {
        ChatGameModule.Default(coreModule, bukkitCoreModule)
    }
    val economyModule: EconomyModule by lazy {
        EconomyModule.Default(coreModule, bukkitCoreModule)
    }
    val restrictionModule: RestrictionModule by lazy {
        RestrictionModule(coreModule, bukkitCoreModule)
    }
    val treeCapitatorModule: TreeCapitatorModule by lazy {
        TreeCapitatorModule(coreModule, bukkitCoreModule)
    }
    val inventorySortModule: InventorySortModule by lazy {
        InventorySortModule(bukkitCoreModule)
    }
    val jailModule: JailModule by lazy {
        JailModule(coreModule, bukkitCoreModule)
    }
}
