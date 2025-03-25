package ru.astrainteractive.aspekt.module.jail.job

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.job.ScheduledJob
import ru.astrainteractive.aspekt.module.jail.controller.JailController
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApi
import ru.astrainteractive.aspekt.module.jail.data.JailApi
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

internal class UnJailJob(
    private val scope: CoroutineScope,
    private val cachedJailApi: CachedJailApi,
    private val jailApi: JailApi,
    private val jailController: JailController
) : ScheduledJob("AspeKt-UnJail") {
    override val delayMillis: Long = 10.seconds.inWholeMilliseconds

    override val initialDelayMillis: Long = 0.seconds.inWholeMilliseconds

    override val isEnabled: Boolean = true

    override fun execute() {
        scope.launch {
            val inmatesToFree = jailApi.getInmates()
                .getOrNull()
                .orEmpty()
                .filter { inmate -> Instant.now() < inmate.start.plusMillis(inmate.duration.inWholeMilliseconds) }

            inmatesToFree.forEach { inmate ->
                jailApi.free(inmate.uuid)
                cachedJailApi.cache(inmate.uuid)
                jailController.free(inmate)
            }
        }
    }
}
