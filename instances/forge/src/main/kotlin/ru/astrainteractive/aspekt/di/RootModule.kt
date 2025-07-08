package ru.astrainteractive.aspekt.di

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.fml.loading.FMLPaths
import ru.astrainteractive.aspekt.module.auth.api.di.AuthApiModule
import ru.astrainteractive.aspekt.module.auth.di.ForgeAuthModule
import ru.astrainteractive.aspekt.module.claims.di.ClaimModule
import ru.astrainteractive.aspekt.module.claims.di.ForgeClaimModule
import ru.astrainteractive.aspekt.module.rtp.di.RtpModule
import ru.astrainteractive.aspekt.module.sethome.di.HomesModule
import ru.astrainteractive.aspekt.module.tpa.di.TpaModule
import ru.astrainteractive.astralibs.coroutine.ForgeMainDispatcher
import ru.astrainteractive.astralibs.event.flowEvent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.astralibs.server.ForgeMinecraftNativeBridge
import ru.astrainteractive.astralibs.server.ForgePlatformServer
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.util.asCachedKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.DefaultKotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.io.File

class RootModule : Logger by JUtiltLogger("AspeKt-RootModuleImpl") {
    val dataFolder by lazy {
        FMLPaths.CONFIGDIR.get().resolve("AspeKt").toAbsolutePath().toFile().also(File::mkdirs)
    }
    val dispatchers = DefaultKotlinDispatchers
    val scope = CoroutineScope(SupervisorJob() + dispatchers.IO)
    val kyoriKrate = DefaultMutableKrate<KyoriComponentSerializer>(
        loader = { null },
        factory = { KyoriComponentSerializer.Legacy }
    ).asCachedKrate()

    @Suppress("UnusedPrivateProperty")
    private val serverStateFlow = flowEvent<ServerStartedEvent>()
        .map { event -> event.server }
        .stateIn(scope, SharingStarted.Eagerly, null)

    private val registerCommandsEvent = flowEvent<RegisterCommandsEvent>(EventPriority.HIGHEST)
        .filterNotNull()
        .stateIn(scope, SharingStarted.Eagerly, null)

    val authApiModule = AuthApiModule(
        scope = scope,
        dataFolder = dataFolder
            .resolve("auth")
            .also(File::mkdirs),
        stringFormat = YamlStringFormat(
            configuration = Yaml.default.configuration.copy(
                encodeDefaults = true,
                strictMode = false,
                polymorphismStyle = PolymorphismStyle.Property
            )
        )
    )
    val forgeAuthModule by lazy {
        ForgeAuthModule(
            scope = scope,
            authApiModule = authApiModule,
            kyoriKrate = kyoriKrate,
            registerCommandsEventFlow = registerCommandsEvent.filterNotNull(),
        )
    }
    val coreModule by lazy {
        CoreModule.Default(
            dataFolder = dataFolder,
            dispatchers = object : KotlinDispatchers {
                override val Main: CoroutineDispatcher = ForgeMainDispatcher
                override val IO: CoroutineDispatcher = Dispatchers.IO
                override val Default: CoroutineDispatcher = Dispatchers.Default
                override val Unconfined: CoroutineDispatcher = Dispatchers.Unconfined
            },
            minecraftNativeBridge = ForgeMinecraftNativeBridge(),
            platformServer = ForgePlatformServer
        )
    }

    val claimModule by lazy {
        ClaimModule(
            stringFormat = coreModule.jsonStringFormat,
            dataFolder = dataFolder,
            scope = scope,
            translationKrate = coreModule.translation
        )
    }

    val forgeClaimModule by lazy {
        ForgeClaimModule(
            registerCommandsEventFlow = registerCommandsEvent.filterNotNull(),
            coreModule = coreModule,
            claimModule = claimModule
        )
    }

    val homesModule by lazy {
        HomesModule(
            registerCommandsEventFlow = registerCommandsEvent.filterNotNull(),
            dataFolder = dataFolder,
            stringFormat = coreModule.jsonStringFormat,
            coreModule = coreModule
        )
    }

    val tpaModule by lazy {
        TpaModule(
            coreModule = coreModule,
            registerCommandsEventFlow = registerCommandsEvent.filterNotNull(),
        )
    }
    val rtpModule by lazy {
        RtpModule(
            coreModule = coreModule,
            registerCommandsEventFlow = registerCommandsEvent.filterNotNull(),
        )
    }

    private val lifecycles: List<Lifecycle>
        get() = listOf(
            coreModule.lifecycle,
            forgeAuthModule.lifecycle,
            forgeClaimModule.lifecycle,
            homesModule.lifecycle,
            tpaModule.lifecycle,
            rtpModule.lifecycle
        )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            lifecycles.forEach(Lifecycle::onEnable)
        },
        onReload = {
            lifecycles.forEach(Lifecycle::onReload)
        },
        onDisable = {
            lifecycles.forEach(Lifecycle::onDisable)
            scope.cancel()
        }
    )
}
