package ru.astrainteractive.aspekt.module.claims.event

import net.neoforged.bus.api.ICancellableEvent
import ru.astrainteractive.aspekt.module.claims.debounce.api.SharedCancellableEvent

internal class ForgeSharedCancellableEvent<T>(
    private val instance: T
) : SharedCancellableEvent where T : ICancellableEvent {
    override var isCancelled: Boolean
        get() = instance.isCanceled
        set(value) {
            instance.isCanceled = value
        }
}
