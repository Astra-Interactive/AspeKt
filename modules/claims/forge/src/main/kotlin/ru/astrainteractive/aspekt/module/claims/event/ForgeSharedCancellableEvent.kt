package ru.astrainteractive.aspekt.module.claims.event

import net.minecraftforge.eventbus.api.Event

class ForgeSharedCancellableEvent<T>(
    private val instance: T
) : SharedCancellableEvent where T : Event {
    override var isCancelled: Boolean
        get() = instance.isCanceled
        set(value) {
            instance.isCanceled = value
        }
}
