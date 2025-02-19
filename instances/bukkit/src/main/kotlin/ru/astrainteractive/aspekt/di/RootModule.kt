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
import ru.astrainteractive.aspekt.module.menu.di.MenuModule
import ru.astrainteractive.aspekt.module.moneyadvancement.di.MoneyAdvancementModule
import ru.astrainteractive.aspekt.module.moneydrop.di.MoneyDropModule
import ru.astrainteractive.aspekt.module.newbee.di.NewBeeModule
import ru.astrainteractive.aspekt.module.restrictions.di.RestrictionModule
import ru.astrainteractive.aspekt.module.sit.di.SitModule
import ru.astrainteractive.aspekt.module.towny.discord.di.TownyDiscordModule
import ru.astrainteractive.aspekt.module.treecapitator.di.TreeCapitatorModule
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin

class RootModule(plugin: LifecyclePlugin) {
    val coreModule: CoreModule by lazy {
        CoreModule.Default(plugin)
    }
    val adminPrivateModule: AdminPrivateModule by lazy {
        AdminPrivateModule.Default(coreModule)
    }
    val menuModule: MenuModule by lazy {
        MenuModule.Default(coreModule)
    }
    val sitModule: SitModule by lazy {
        SitModule.Default(coreModule)
    }
    val entitiesModule: EntitiesModule by lazy {
        EntitiesModule(coreModule)
    }
    val autoBroadcastModule by lazy {
        AutoBroadcastModule.Default(coreModule)
    }
    val discordLinkModule: DiscordLinkModule by lazy {
        DiscordLinkModule.Default(coreModule)
    }

    val commandManagerModule: CommandManagerModule by lazy {
        CommandManagerModule.Default(coreModule = coreModule)
    }
    val townyDiscordModule: TownyDiscordModule by lazy {
        TownyDiscordModule.Default(coreModule, discordLinkModule)
    }
    val moneyDropModule: MoneyDropModule by lazy {
        MoneyDropModule.Default(coreModule)
    }
    val autoCropModule: AutoCropModule by lazy {
        AutoCropModule.Default(coreModule)
    }
    val newBeeModule: NewBeeModule by lazy {
        NewBeeModule.Default(coreModule = coreModule)
    }
    val antiSwearModule: AntiSwearModule by lazy {
        AntiSwearModule.Default(coreModule = coreModule)
    }
    val moneyAdvancementModule: MoneyAdvancementModule by lazy {
        MoneyAdvancementModule.Default(coreModule)
    }
    val chatGameModule: ChatGameModule by lazy {
        ChatGameModule.Default(coreModule = coreModule)
    }
    val economyModule: EconomyModule by lazy {
        EconomyModule.Default(coreModule)
    }
    val restrictionModule: RestrictionModule by lazy {
        RestrictionModule(coreModule)
    }
    val treeCapitatorModule: TreeCapitatorModule by lazy {
        TreeCapitatorModule(coreModule)
    }
    val inventorySortModule: InventorySortModule by lazy {
        InventorySortModule(coreModule)
    }
}
