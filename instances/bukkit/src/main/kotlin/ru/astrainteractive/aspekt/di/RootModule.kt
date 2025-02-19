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

interface RootModule {
    val coreModule: CoreModule
    val adminPrivateModule: AdminPrivateModule
    val sitModule: SitModule
    val menuModule: MenuModule
    val entitiesModule: EntitiesModule
    val autoBroadcastModule: AutoBroadcastModule
    val discordLinkModule: DiscordLinkModule
    val commandManagerModule: CommandManagerModule
    val townyDiscordModule: TownyDiscordModule
    val moneyDropModule: MoneyDropModule
    val autoCropModule: AutoCropModule
    val newBeeModule: NewBeeModule
    val antiSwearModule: AntiSwearModule
    val moneyAdvancementModule: MoneyAdvancementModule
    val chatGameModule: ChatGameModule
    val economyModule: EconomyModule
    val treeCapitatorModule: TreeCapitatorModule
    val restrictionModule: RestrictionModule
    val inventorySortModule: InventorySortModule
}
