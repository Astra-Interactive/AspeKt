package ru.astrainteractive.aspekt

import kotlinx.coroutines.MainCoroutineDispatcher
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class BungeeMainDispatcher(private val plugin: Plugin) : MainCoroutineDispatcher() {
    override val immediate: MainCoroutineDispatcher get() = this

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        ProxyServer.getInstance().scheduler.schedule(plugin, {
            block.run()
        }, 0L, TimeUnit.MILLISECONDS)
    }
}
