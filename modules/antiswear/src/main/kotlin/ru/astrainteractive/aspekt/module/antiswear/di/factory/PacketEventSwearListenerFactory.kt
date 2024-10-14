package ru.astrainteractive.aspekt.module.antiswear.di.factory

import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepository
import ru.astrainteractive.aspekt.module.antiswear.event.PacketEventSwearListener
import ru.astrainteractive.astralibs.event.EventListener

internal class PacketEventSwearListenerFactory(
    private val swearRepository: SwearRepository
) {
    fun create(): EventListener {
        return PacketEventSwearListener(swearRepository)
    }
}
