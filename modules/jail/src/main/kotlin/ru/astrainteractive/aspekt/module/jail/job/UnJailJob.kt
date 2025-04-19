package ru.astrainteractive.aspekt.module.jail.job

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.job.ScheduledJob
import ru.astrainteractive.aspekt.module.jail.controller.JailController
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApi
import ru.astrainteractive.aspekt.module.jail.data.JailApi
import ru.astrainteractive.aspekt.module.jail.util.offlinePlayer
import ru.astrainteractive.aspekt.module.jail.util.sendMessage
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.util.getValue
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

internal class UnJailJob(
    private val scope: CoroutineScope,
    private val cachedJailApi: CachedJailApi,
    private val jailApi: JailApi,
    private val jailController: JailController,
    kyoriKrate: Krate<KyoriComponentSerializer>,
    translationKrate: Krate<PluginTranslation>
) : ScheduledJob("AspeKt-UnJail") {
    override val delayMillis: Long = 10.seconds.inWholeMilliseconds
    override val initialDelayMillis: Long = 0.seconds.inWholeMilliseconds
    override val isEnabled: Boolean = true
    private val kyori by kyoriKrate
    private val translation by translationKrate

    override fun execute() {
        scope.launch {
            val inmatesToFree = jailApi.getInmates()
                .getOrNull()
                .orEmpty()
                .filter { inmate ->
                    val diff = Instant.now().epochSecond.minus(inmate.start.epochSecond)
                    diff > inmate.duration.inWholeSeconds
                }

            inmatesToFree.forEach { inmate ->
                jailApi.free(inmate.uuid)
                cachedJailApi.cache(inmate.uuid)
                jailController.free(inmate)
                with(kyori) {
                    inmate.offlinePlayer.sendMessage(translation.jails.youVeBeenFreed.component)
                }
            }
        }
    }
}
