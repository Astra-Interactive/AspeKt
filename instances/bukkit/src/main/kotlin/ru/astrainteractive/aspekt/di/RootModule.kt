package ru.astrainteractive.aspekt.di

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import ru.astrainteractive.aspekt.command.di.BukkitCommandsModule
import ru.astrainteractive.aspekt.command.di.CommandsModule
import ru.astrainteractive.aspekt.feature.flagreader.FileFeatureFlagReader
import ru.astrainteractive.aspekt.feature.gate.FeatureGate
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
import ru.astrainteractive.aspekt.module.oregeneration.di.OreGenerationModule
import ru.astrainteractive.aspekt.module.playtimereward.di.PlaytimeRewardModule
import ru.astrainteractive.aspekt.module.restrictions.di.RestrictionModule
import ru.astrainteractive.aspekt.module.sit.di.SitModule
import ru.astrainteractive.aspekt.module.treecapitator.di.TreeCapitatorModule
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.brigadier.command.PaperMultiplatformCommands
import ru.astrainteractive.astralibs.command.api.registrar.PaperCommandRegistrarContext
import ru.astrainteractive.astralibs.coroutines.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astralibs.server.bridge.BukkitPlatformServer

class RootModule(plugin: LifecyclePlugin) {
    private val coreModule: CoreModule = CoreModule(
        dataFolder = plugin.dataFolder,
        dispatchers = DefaultBukkitDispatchers(plugin),
        platformServer = BukkitPlatformServer(),
        multiplatformCommand = MultiplatformCommand(PaperMultiplatformCommands()),
        commandRegistrarContextFactory = { coroutineScope -> PaperCommandRegistrarContext(coroutineScope, plugin) }
    )
    private val bukkitCoreModule: BukkitCoreModule = BukkitCoreModule(
        plugin = plugin,
        mainScope = coreModule.mainScope
    )

    private val menuModuleGate = FeatureGate(
        featureClass = MenuModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = MenuModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { MenuModule(coreModule, bukkitCoreModule) },
        lifecycleSelector = MenuModule::lifecycle
    )
    private val autoBroadcastModuleGate = FeatureGate(
        featureClass = AutoBroadcastModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = AutoBroadcastModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { AutoBroadcastModule(coreModule) },
        lifecycleSelector = AutoBroadcastModule::lifecycle
    )
    private val sitModuleGate = FeatureGate(
        featureClass = SitModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = SitModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { SitModule(coreModule, bukkitCoreModule) },
        lifecycleSelector = SitModule::lifecycle
    )
    private val claimModuleGate = FeatureGate(
        featureClass = BukkitClaimModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = ClaimModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = {
            BukkitClaimModule(
                coreModule = coreModule,
                bukkitCoreModule = bukkitCoreModule,
                claimModule = ClaimModule(
                    stringFormat = coreModule.jsonStringFormat,
                    dataFolder = coreModule.dataFolder,
                    ioScope = coreModule.ioScope,
                    translationKrate = coreModule.translationKrate
                )
            )
        },
        lifecycleSelector = BukkitClaimModule::lifecycle
    )
    private val moneyDropModuleGate = FeatureGate(
        featureClass = MoneyDropModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = MoneyDropModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { MoneyDropModule(coreModule, bukkitCoreModule) },
        lifecycleSelector = MoneyDropModule::lifecycle
    )
    private val playtimeRewardModuleGate = FeatureGate(
        featureClass = PlaytimeRewardModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = PlaytimeRewardModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { PlaytimeRewardModule(coreModule, bukkitCoreModule) },
        lifecycleSelector = PlaytimeRewardModule::lifecycle
    )
    private val autoCropModuleGate = FeatureGate(
        featureClass = AutoCropModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = AutoCropModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { AutoCropModule(coreModule, bukkitCoreModule) },
        lifecycleSelector = AutoCropModule::lifecycle
    )
    private val newBeeModuleGate = FeatureGate(
        featureClass = NewBeeModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = NewBeeModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { NewBeeModule(coreModule, bukkitCoreModule) },
        lifecycleSelector = NewBeeModule::lifecycle
    )
    private val antiSwearModuleGate = FeatureGate(
        featureClass = AntiSwearModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = AntiSwearModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { AntiSwearModule(coreModule, bukkitCoreModule) },
        lifecycleSelector = AntiSwearModule::lifecycle
    )
    private val moneyAdvancementModuleGate = FeatureGate(
        featureClass = MoneyAdvancementModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = MoneyAdvancementModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { MoneyAdvancementModule(coreModule, bukkitCoreModule) },
        lifecycleSelector = MoneyAdvancementModule::lifecycle
    )
    private val chatGameModuleGate = FeatureGate(
        featureClass = ChatGameModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = ChatGameModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { ChatGameModule(coreModule, bukkitCoreModule) },
        lifecycleSelector = ChatGameModule::lifecycle
    )
    private val restrictionModuleGate = FeatureGate(
        featureClass = RestrictionModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = RestrictionModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { RestrictionModule(coreModule, bukkitCoreModule) },
        lifecycleSelector = RestrictionModule::lifecycle
    )
    private val oreGenerationModuleGate = FeatureGate(
        featureClass = OreGenerationModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = OreGenerationModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { OreGenerationModule(coreModule, bukkitCoreModule) },
        lifecycleSelector = OreGenerationModule::lifecycle
    )
    private val treeCapitatorModuleGate = FeatureGate(
        featureClass = TreeCapitatorModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = TreeCapitatorModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { TreeCapitatorModule(coreModule, bukkitCoreModule) },
        lifecycleSelector = TreeCapitatorModule::lifecycle
    )
    private val inventorySortModuleGate = FeatureGate(
        featureClass = InventorySortModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = InventorySortModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { InventorySortModule(bukkitCoreModule) },
        lifecycleSelector = InventorySortModule::lifecycle
    )
    private val jailModuleGate = FeatureGate(
        featureClass = JailModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = JailModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { JailModule(coreModule, bukkitCoreModule) },
        lifecycleSelector = JailModule::lifecycle
    )
    private val invisibleItemFrameModuleGate = FeatureGate(
        featureClass = InvisibleItemFrameModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = InvisibleItemFrameModule.getConfigurationFile(coreModule.dataFolder)
        ),
        featureFactory = { InvisibleItemFrameModule(bukkitCoreModule) },
        lifecycleSelector = InvisibleItemFrameModule::lifecycle
    )

    private val bukkitCommandsModule by lazy {
        BukkitCommandsModule(
            bukkitCoreModule = bukkitCoreModule,
            coreModule = coreModule
        )
    }
    private val commandModule by lazy {
        CommandsModule(
            coreModule = coreModule,
            lifecyclePlugin = plugin
        )
    }

    private val lifecycles: List<Lifecycle>
        get() = listOf(
            coreModule.lifecycle,
            bukkitCoreModule.lifecycle,
            bukkitCommandsModule.lifecycle,
            commandModule.lifecycle,
            menuModuleGate,
            autoBroadcastModuleGate,
            sitModuleGate,
            claimModuleGate,
            moneyDropModuleGate,
            playtimeRewardModuleGate,
            autoCropModuleGate,
            newBeeModuleGate,
            antiSwearModuleGate,
            moneyAdvancementModuleGate,
            chatGameModuleGate,
            treeCapitatorModuleGate,
            restrictionModuleGate,
            oreGenerationModuleGate,
            inventorySortModuleGate,
            jailModuleGate,
            invisibleItemFrameModuleGate
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
