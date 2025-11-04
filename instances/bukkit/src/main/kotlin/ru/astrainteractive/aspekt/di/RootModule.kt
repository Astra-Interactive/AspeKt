package ru.astrainteractive.aspekt.di

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import ru.astrainteractive.aspekt.command.di.CommonCommandsModule
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
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astralibs.server.BukkitMinecraftNativeBridge
import ru.astrainteractive.astralibs.server.BukkitPlatformServer

class RootModule(plugin: LifecyclePlugin) {
    private val coreModule: CoreModule by lazy {
        CoreModule(
            dataFolder = plugin.dataFolder,
            dispatchers = DefaultBukkitDispatchers(plugin),
            minecraftNativeBridge = BukkitMinecraftNativeBridge(),
            platformServer = BukkitPlatformServer()
        )
    }
    private val bukkitCoreModule: BukkitCoreModule = BukkitCoreModule(
        plugin = plugin,
        ioScope = coreModule.ioScope,
        mainScope = coreModule.mainScope
    )
    private val claimModule by lazy {
        ClaimModule(
            stringFormat = coreModule.jsonStringFormat,
            dataFolder = coreModule.dataFolder,
            ioScope = coreModule.ioScope,
            translationKrate = coreModule.translation
        )
    }
    private val bukkitClaimModule: BukkitClaimModule by lazy {
        BukkitClaimModule(
            coreModule = coreModule,
            bukkitCoreModule = bukkitCoreModule,
            claimModule = claimModule
        )
    }
    private val menuModule: MenuModule by lazy {
        MenuModule(coreModule, bukkitCoreModule)
    }
    private val sitModule: SitModule by lazy {
        SitModule(coreModule, bukkitCoreModule)
    }
    private val autoBroadcastModule by lazy {
        AutoBroadcastModule(coreModule)
    }
    private val commonCommandModule: CommonCommandsModule by lazy {
        CommonCommandsModule(coreModule, bukkitCoreModule)
    }
    private val moneyDropModule: MoneyDropModule by lazy {
        MoneyDropModule(coreModule, bukkitCoreModule)
    }
    private val autoCropModule: AutoCropModule by lazy {
        AutoCropModule(coreModule, bukkitCoreModule)
    }
    private val newBeeModule: NewBeeModule by lazy {
        NewBeeModule(coreModule, bukkitCoreModule)
    }
    private val antiSwearModule: AntiSwearModule by lazy {
        AntiSwearModule(coreModule, bukkitCoreModule)
    }
    private val moneyAdvancementModule: MoneyAdvancementModule by lazy {
        MoneyAdvancementModule(coreModule, bukkitCoreModule)
    }
    private val chatGameModule: ChatGameModule by lazy {
        ChatGameModule(coreModule, bukkitCoreModule)
    }
    private val restrictionModule: RestrictionModule by lazy {
        RestrictionModule(coreModule, bukkitCoreModule)
    }
    private val treeCapitatorModule: TreeCapitatorModule by lazy {
        TreeCapitatorModule(coreModule, bukkitCoreModule)
    }
    private val inventorySortModule: InventorySortModule by lazy {
        InventorySortModule(bukkitCoreModule)
    }
    private val jailModule: JailModule by lazy {
        JailModule(coreModule, bukkitCoreModule)
    }
    private val invisibleItemFrameModule by lazy {
        InvisibleItemFrameModule(bukkitCoreModule)
    }

    private val lifecycles: List<Lifecycle>
        get() = listOfNotNull(
            coreModule.lifecycle,
            bukkitCoreModule.lifecycle,
            menuModule.lifecycle,
            autoBroadcastModule.lifecycle,
            sitModule.lifecycle,
            commonCommandModule.lifecycle,
            bukkitClaimModule.lifecycle,
            moneyDropModule.lifecycle,
            autoCropModule.lifecycle,
            newBeeModule.lifecycle,
            antiSwearModule.lifecycle,
            moneyAdvancementModule.lifecycle,
            chatGameModule.lifecycle,
            treeCapitatorModule.lifecycle,
            restrictionModule.lifecycle,
            inventorySortModule.lifecycle,
            jailModule.lifecycle,
            invisibleItemFrameModule.lifecycle
        )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            lifecycles.forEach(Lifecycle::onEnable)
        },
        onReload = {
            lifecycles.forEach(Lifecycle::onReload)
            Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
        },
        onDisable = {
            lifecycles.forEach(Lifecycle::onDisable)
            HandlerList.unregisterAll(bukkitCoreModule.plugin)
            Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
        }
    )
}
