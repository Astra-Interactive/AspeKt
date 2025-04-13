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
import ru.astrainteractive.aspekt.core.forge.coroutine.ForgeMainDispatcher
import ru.astrainteractive.aspekt.core.forge.event.flowEvent
import ru.astrainteractive.aspekt.module.auth.api.di.AuthApiModule
import ru.astrainteractive.aspekt.module.auth.di.ForgeAuthModule
import ru.astrainteractive.aspekt.module.claims.di.ForgeClaimModule
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
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
    )

    @Suppress("UnusedPrivateProperty")
    private val serverStateFlow = flowEvent<ServerStartedEvent>()
        .map { event -> event.server }
        .stateIn(scope, SharingStarted.Eagerly, null)

    private val registerCommandsEvent = flowEvent<RegisterCommandsEvent>(EventPriority.HIGHEST)
        .stateIn(scope, SharingStarted.Eagerly, null)

    val authApiModule = AuthApiModule(
        scope = scope,
        dataFolder = dataFolder,
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
            }
        )
    }

    val forgeClaimModule by lazy {
        ForgeClaimModule(
            registerCommandsEventFlow = registerCommandsEvent.filterNotNull(),
            serverFlow = serverStateFlow.filterNotNull(),
            coreModule = coreModule
        )
    }

    private val lifecycles: List<Lifecycle>
        get() = listOf(
            coreModule.lifecycle,
            forgeAuthModule.lifecycle,
            forgeClaimModule.lifecycle
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
