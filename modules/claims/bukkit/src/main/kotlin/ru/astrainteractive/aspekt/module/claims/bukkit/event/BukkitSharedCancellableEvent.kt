package ru.astrainteractive.aspekt.module.claims.bukkit.event

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import ru.astrainteractive.aspekt.module.claims.api.debounce.api.SharedCancellableEvent

internal class BukkitSharedCancellableEvent<T>(private val instance: T) :
    SharedCancellableEvent where T : Event, T : Cancellable {
    override var isCancelled: Boolean
        get() = instance.isCancelled
        set(value) {
            instance.isCancelled = value
        }
}
