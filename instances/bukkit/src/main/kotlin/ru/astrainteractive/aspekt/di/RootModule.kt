package ru.astrainteractive.aspekt.di

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.command.di.CommandManagerModule
import ru.astrainteractive.aspekt.inventorysort.di.InventorySortModule
import ru.astrainteractive.aspekt.invisibleframes.di.InvisibleItemFrameModule
import ru.astrainteractive.aspekt.minecraft.messenger.MinecraftMessenger
import ru.astrainteractive.aspekt.minecraft.player.MinecraftPlayer
import ru.astrainteractive.aspekt.module.antiswear.di.AntiSwearModule
import ru.astrainteractive.aspekt.module.autobroadcast.di.AutoBroadcastModule
import ru.astrainteractive.aspekt.module.autocrop.di.AutoCropModule
import ru.astrainteractive.aspekt.module.chatgame.di.ChatGameModule
import ru.astrainteractive.aspekt.module.claims.command.discordlink.di.DiscordLinkModule
import ru.astrainteractive.aspekt.module.claims.di.BukkitClaimModule
import ru.astrainteractive.aspekt.module.claims.di.ClaimModule
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
import ru.astrainteractive.astralibs.string.StringDesc
import java.util.UUID

class RootModule(plugin: LifecyclePlugin) {
    val coreModule: CoreModule by lazy {
        CoreModule.Default(
            dataFolder = plugin.dataFolder,
            dispatchers = DefaultBukkitDispatchers(plugin),
            createMinecraftMessenger = { kyoriKrate ->
                object : MinecraftMessenger {
                    override fun send(
                        player: MinecraftPlayer,
                        stringDesc: StringDesc
                    ): Unit = with(kyoriKrate.cachedValue) {
                        Bukkit.getPlayer(player.uuid)?.sendMessage(stringDesc.component)
                    }

                    override fun send(
                        uuid: UUID,
                        stringDesc: StringDesc
                    ): Unit = with(kyoriKrate.cachedValue) {
                        Bukkit.getPlayer(uuid)?.sendMessage(stringDesc.component)
                    }
                }
            }
        )
    }
    val bukkitCoreModule: BukkitCoreModule by lazy {
        BukkitCoreModule(plugin)
    }
    val claimModule by lazy {
        ClaimModule(
            stringFormat = coreModule.jsonStringFormat,
            dataFolder = coreModule.dataFolder,
            scope = coreModule.scope,
            translationKrate = coreModule.translation
        )
    }
    val bukkitClaimModule: BukkitClaimModule by lazy {
        BukkitClaimModule.Default(
            coreModule = coreModule,
            bukkitCoreModule = bukkitCoreModule,
            claimModule = claimModule
        )
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
    val invisibleItemFrameModule by lazy {
        InvisibleItemFrameModule(bukkitCoreModule)
    }
}
