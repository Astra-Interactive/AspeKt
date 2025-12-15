package ru.astrainteractive.aspekt.module.claims.event

import net.neoforged.bus.api.ICancellableEvent

class ForgeSharedCancellableEvent<T>(
    private val instance: T
) : SharedCancellableEvent where T : ICancellableEvent {
    override var isCancelled: Boolean
        get() = instance.isCanceled
        set(value) {
            instance.isCanceled = value
        }
}
