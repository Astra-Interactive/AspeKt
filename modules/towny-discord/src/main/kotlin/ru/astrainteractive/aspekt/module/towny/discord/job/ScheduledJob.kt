package ru.astrainteractive.aspekt.module.towny.discord.job

import java.util.Timer

internal abstract class ScheduledJob(val key: String) {
    private var scheduler: Timer? = null
    protected abstract val delayMillis: Long
    protected abstract val initialDelayMillis: Long
    protected abstract val isEnabled: Boolean
    protected abstract fun execute()

    fun onEnable() {
        if (!isEnabled) return
        scheduler = kotlin.concurrent.timer(key, false, initialDelayMillis, delayMillis) {
            execute()
        }
    }

    fun onDisable() {
        scheduler?.cancel()
        scheduler = null
    }
}
