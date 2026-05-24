package ru.astrainteractive.aspekt.di

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.util.YamlStringFormat
import ru.astrainteractive.klibs.kstorage.api.asCachedKrate
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.api.withDefault
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import java.io.File

class CoreModule(
    val dataFolder: File,
    val dispatchers: KotlinDispatchers,
    val platformServer: PlatformServer,
    val multiplatformCommand: MultiplatformCommand
) {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val logger = JUtiltLogger("CoroutineExceptionHandler-AspeKt")
        logger.error(throwable) { "Error happened inside global coroutine scope!" }
    }

    val ioScope = CoroutineFeature
        .Default(dispatchers.IO + SupervisorJob() + coroutineExceptionHandler)
        .withTimings()

    val mainScope: CoroutineScope = CoroutineFeature
        .Default(dispatchers.Main + SupervisorJob() + coroutineExceptionHandler)
        .withTimings()

    val unconfinedScope = CoroutineFeature
        .Default(dispatchers.Unconfined + SupervisorJob() + coroutineExceptionHandler)
        .withTimings()

    val yamlFormat: StringFormat = YamlStringFormat(
        configuration = Yaml.default.configuration.copy(
            encodeDefaults = true,
            strictMode = false,
            polymorphismStyle = PolymorphismStyle.Property
        ),
    )

    val translationKrate = yamlFormat
        .krateOf<PluginTranslation>(dataFolder.resolve("translations.yml"))
        .withDefault(::PluginTranslation)
        .asCachedMutableKrate()

    val kyoriKrate = DefaultMutableKrate<KyoriComponentSerializer>(
        loader = { null },
        factory = { KyoriComponentSerializer.Legacy }
    ).asCachedKrate()

    val jsonStringFormat: StringFormat = Json {
        isLenient = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {},
        onReload = {
            translationKrate.getValue()
        },
        onDisable = {
            ioScope.cancel()
        }
    )
}
