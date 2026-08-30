package ru.astrainteractive.aspekt.di

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import net.neoforged.fml.loading.FMLPaths
import ru.astrainteractive.aspekt.command.di.CommandsModule
import ru.astrainteractive.aspekt.feature.flagreader.FileFeatureFlagReader
import ru.astrainteractive.aspekt.feature.gate.FeatureGate
import ru.astrainteractive.aspekt.feature.gate.mapEnabled
import ru.astrainteractive.aspekt.module.auth.api.di.AuthApiModule
import ru.astrainteractive.aspekt.module.auth.di.ForgeAuthModule
import ru.astrainteractive.aspekt.module.claims.di.ClaimModule
import ru.astrainteractive.aspekt.module.claims.di.NeoForgeClaimModule
import ru.astrainteractive.aspekt.module.rtp.api.MinecraftSafeLocationProvider
import ru.astrainteractive.aspekt.module.rtp.di.RtpModule
import ru.astrainteractive.aspekt.module.sethome.di.SetHomeModule
import ru.astrainteractive.aspekt.module.tpa.di.TpaModule
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.brigadier.command.MinecraftMultiplatformCommands
import ru.astrainteractive.astralibs.command.registrar.NeoForgeCommandRegistrarContext
import ru.astrainteractive.astralibs.coroutines.MinecraftDispatchers
import ru.astrainteractive.astralibs.lifecycle.ForgeLifecycleServer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.MinecraftPlatformServer
import ru.astrainteractive.astralibs.util.YamlStringFormat
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.io.File

class RootModule(forgeLifecycleServer: ForgeLifecycleServer) : Logger by JUtiltLogger("AspeKt-RootModuleImpl") {
    private val dataFolder by lazy {
        FMLPaths.CONFIGDIR.get()
            .resolve("AspeKt")
            .toAbsolutePath()
            .toFile()
            .also(File::mkdirs)
    }
    private val coreModule = CoreModule(
        dataFolder = dataFolder,
        dispatchers = MinecraftDispatchers(),
        platformServer = MinecraftPlatformServer,
        multiplatformCommand = MultiplatformCommand(MinecraftMultiplatformCommands()),
        commandRegistrarContextFactory = ::NeoForgeCommandRegistrarContext
    )
    private val commandModule by lazy {
        CommandsModule(
            coreModule = coreModule,
            lifecyclePlugin = forgeLifecycleServer
        )
    }
    private val authDataFolder by lazy {
        dataFolder
            .resolve("auth")
            .also(File::mkdirs)
    }
    private val authStringFormat = YamlStringFormat(
        configuration = Yaml.default.configuration.copy(
            encodeDefaults = true,
            strictMode = false,
            polymorphismStyle = PolymorphismStyle.Property
        )
    )
    private val authApiModuleGate = FeatureGate(
        featureClass = AuthApiModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = authStringFormat,
            file = AuthApiModule.getConfigurationFile(authDataFolder)
        ),
        featureFactory = {
            AuthApiModule(
                ioScope = coreModule.ioScope,
                dataFolder = authDataFolder,
                stringFormat = authStringFormat
            )
        },
        lifecycleSelector = AuthApiModule::lifecycle
    )

    private val forgeAuthModuleGate = authApiModuleGate.mapEnabled(
        featureClass = ForgeAuthModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = authStringFormat,
            file = ForgeAuthModule.getConfigurationFile(authDataFolder)
        ),
        lifecycleSelector = ForgeAuthModule::lifecycle,
        transform = { authApiModule ->
            ForgeAuthModule(
                authApiModule = authApiModule,
                coreModule = coreModule,
                commandRegistrarContext = coreModule.commandRegistrarContext
            )
        }
    )

    private val neoForgeClaimModuleGate = FeatureGate(
        featureClass = NeoForgeClaimModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = ClaimModule.getConfigurationFile(dataFolder)
        ),
        featureFactory = {
            NeoForgeClaimModule(
                commandRegistrarContext = coreModule.commandRegistrarContext,
                coreModule = coreModule,
                claimModule = ClaimModule(
                    stringFormat = coreModule.jsonStringFormat,
                    dataFolder = dataFolder,
                    ioScope = coreModule.ioScope,
                    translationKrate = coreModule.translationKrate
                )
            )
        },
        lifecycleSelector = NeoForgeClaimModule::lifecycle
    )

    private val setHomeModuleGate = FeatureGate(
        featureClass = SetHomeModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = SetHomeModule.getConfigurationFile(dataFolder)
        ),
        featureFactory = {
            SetHomeModule(
                commandRegistrarContext = coreModule.commandRegistrarContext,
                dataFolder = dataFolder,
                stringFormat = coreModule.jsonStringFormat,
                coreModule = coreModule
            )
        },
        lifecycleSelector = SetHomeModule::lifecycle
    )

    private val tpaModuleGate = FeatureGate(
        featureClass = TpaModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = TpaModule.getConfigurationFile(dataFolder)
        ),
        featureFactory = {
            TpaModule(
                coreModule = coreModule,
                commandRegistrarContext = coreModule.commandRegistrarContext,
            )
        },
        lifecycleSelector = TpaModule::lifecycle
    )

    private val rtpModuleGate = FeatureGate(
        featureClass = RtpModule::class,
        flagReader = FileFeatureFlagReader(
            yamlFormat = coreModule.yamlFormat,
            file = RtpModule.getConfigurationFile(dataFolder)
        ),
        featureFactory = {
            RtpModule(
                coreModule = coreModule,
                commandRegistrarContext = coreModule.commandRegistrarContext,
                multiplatformCommand = coreModule.multiplatformCommand,
                safeLocationProviderFactory = { rtpConfigKrate ->
                    MinecraftSafeLocationProvider(
                        rtpConfigKrate = rtpConfigKrate,
                        dispatchers = coreModule.dispatchers
                    )
                },
            )
        },
        lifecycleSelector = RtpModule::lifecycle
    )

    private val lifecycles: List<Lifecycle>
        get() = listOf(
            coreModule.lifecycle,
            commandModule.lifecycle,
            authApiModuleGate,
            forgeAuthModuleGate,
            neoForgeClaimModuleGate,
            setHomeModuleGate,
            tpaModuleGate,
            rtpModuleGate
        )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            lifecycles.forEach(Lifecycle::onEnable)
        },
        onReload = {
            lifecycles.forEach(Lifecycle::onReload)
        },
        onDisable = {
            lifecycles.reversed().forEach(Lifecycle::onDisable)
        }
    )
}
