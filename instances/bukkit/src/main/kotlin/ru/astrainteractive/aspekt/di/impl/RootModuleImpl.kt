package ru.astrainteractive.aspekt.di.impl

import ru.astrainteractive.aspekt.command.di.CommandManagerModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.di.RootModule
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

class RootModuleImpl(plugin: LifecyclePlugin) : RootModule {
    override val coreModule: CoreModule by lazy {
        CoreModule.Default(plugin)
    }
    override val adminPrivateModule: AdminPrivateModule by lazy {
        AdminPrivateModule.Default(coreModule)
    }
    override val menuModule: MenuModule by lazy {
        MenuModule.Default(coreModule)
    }
    override val sitModule: SitModule by lazy {
        SitModule.Default(coreModule)
    }
    override val entitiesModule: EntitiesModule by lazy {
        EntitiesModule(coreModule)
    }
    override val autoBroadcastModule by lazy {
        AutoBroadcastModule.Default(coreModule)
    }
    override val discordLinkModule: DiscordLinkModule by lazy {
        DiscordLinkModule.Default(coreModule)
    }

    override val commandManagerModule: CommandManagerModule by lazy {
        CommandManagerModule.Default(coreModule = coreModule)
    }
    override val townyDiscordModule: TownyDiscordModule by lazy {
        TownyDiscordModule.Default(coreModule, discordLinkModule)
    }
    override val moneyDropModule: MoneyDropModule by lazy {
        MoneyDropModule.Default(coreModule)
    }
    override val autoCropModule: AutoCropModule by lazy {
        AutoCropModule.Default(coreModule)
    }
    override val newBeeModule: NewBeeModule by lazy {
        NewBeeModule.Default(coreModule = coreModule)
    }
    override val antiSwearModule: AntiSwearModule by lazy {
        AntiSwearModule.Default(coreModule = coreModule)
    }
    override val moneyAdvancementModule: MoneyAdvancementModule by lazy {
        MoneyAdvancementModule.Default(coreModule)
    }
    override val chatGameModule: ChatGameModule by lazy {
        ChatGameModule.Default(coreModule = coreModule)
    }
    override val economyModule: EconomyModule by lazy {
        EconomyModule.Default(coreModule)
    }
    override val restrictionModule: RestrictionModule by lazy {
        RestrictionModule(coreModule)
    }
    override val treeCapitatorModule: TreeCapitatorModule by lazy {
        TreeCapitatorModule(coreModule)
    }
    override val inventorySortModule: InventorySortModule by lazy {
        InventorySortModule(coreModule)
    }
}
