package ru.astrainteractive.aspekt.di

import ru.astrainteractive.aspekt.command.di.CommandManagerModule
import ru.astrainteractive.aspekt.inventorysort.di.InventorySortModule
import ru.astrainteractive.aspekt.invisibleframes.di.InvisibleItemFrameModule
import ru.astrainteractive.aspekt.module.antiswear.di.AntiSwearModule
import ru.astrainteractive.aspekt.module.autobroadcast.di.AutoBroadcastModule
import ru.astrainteractive.aspekt.module.autocrop.di.AutoCropModule
import ru.astrainteractive.aspekt.module.chatgame.di.ChatGameModule
import ru.astrainteractive.aspekt.module.claims.di.BukkitClaimModule
import ru.astrainteractive.aspekt.module.claims.di.ClaimModule
import ru.astrainteractive.aspekt.module.jail.di.JailModule
import ru.astrainteractive.aspekt.module.menu.di.MenuModule
import ru.astrainteractive.aspekt.module.moneyadvancement.di.MoneyAdvancementModule
import ru.astrainteractive.aspekt.module.moneydrop.di.MoneyDropModule
import ru.astrainteractive.aspekt.module.newbee.di.NewBeeModule
import ru.astrainteractive.aspekt.module.restrictions.di.RestrictionModule
import ru.astrainteractive.aspekt.module.sit.di.SitModule
import ru.astrainteractive.aspekt.module.treecapitator.di.TreeCapitatorModule
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astralibs.server.BukkitMinecraftNativeBridge
import ru.astrainteractive.astralibs.server.BukkitPlatformServer

class RootModule(plugin: LifecyclePlugin) {
    val coreModule: CoreModule by lazy {
        CoreModule.Default(
            dataFolder = plugin.dataFolder,
            dispatchers = DefaultBukkitDispatchers(plugin),
            minecraftNativeBridge = BukkitMinecraftNativeBridge(),
            platformServer = BukkitPlatformServer()
        )
    }
    val bukkitCoreModule: BukkitCoreModule = BukkitCoreModule(
        plugin = plugin,
        scope = coreModule.scope,
        mainScope = coreModule.mainScope
    )
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
    val autoBroadcastModule by lazy {
        AutoBroadcastModule.Default(coreModule)
    }
    val commandManagerModule: CommandManagerModule by lazy {
        CommandManagerModule.Default(coreModule, bukkitCoreModule)
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
