package ru.astrainteractive.aspekt.module.claims.event

import net.minecraftforge.eventbus.api.Event

internal class ForgeSharedCancellableEvent<T : Event>(
    private val instance: T
) : SharedCancellableEvent {
    override var isCancelled: Boolean
        get() = instance.isCanceled
        set(value) {
            instance.isCanceled = value
        }
}
