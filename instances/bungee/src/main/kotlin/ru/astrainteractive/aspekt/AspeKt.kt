package ru.astrainteractive.aspekt

import kotlinx.coroutines.cancel
import net.md_5.bungee.api.plugin.Plugin
import ru.astrainteractive.aspekt.event.OnlineSimulator
import ru.astrainteractive.aspekt.event.ProxyPingEventListener
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.dispatchers.DefaultKotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class AspeKt : Plugin() {
    private val dispatchers = object : KotlinDispatchers by DefaultKotlinDispatchers {
        override val Main = BungeeMainDispatcher(this@AspeKt)
    }
    private val ioScope = CoroutineFeature.Default(dispatchers.IO)
    private val proxyPingEventListener = ProxyPingEventListener(
        onlineSimulator = OnlineSimulator(ioScope)
    )

    override fun onEnable() {
        proxyPingEventListener.register(this)
    }

    override fun onDisable() {
        proxyPingEventListener.unregister(this)
        ioScope.cancel()
    }
}
