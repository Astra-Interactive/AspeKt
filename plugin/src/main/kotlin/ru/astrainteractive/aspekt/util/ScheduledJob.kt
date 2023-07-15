package ru.astrainteractive.aspekt.util

import java.util.Timer

abstract class ScheduledJob(val key: String) {
    private var scheduler: Timer? = null
    protected abstract val delayMillis: Long
    protected abstract val initialDelayMillis: Long
    protected abstract fun execute()

    fun onEnable() {
        scheduler = kotlin.concurrent.timer(key, false, initialDelayMillis, delayMillis) {
            execute()
        }
    }

    fun onDisable() {
        scheduler?.cancel()
        scheduler = null
    }
}