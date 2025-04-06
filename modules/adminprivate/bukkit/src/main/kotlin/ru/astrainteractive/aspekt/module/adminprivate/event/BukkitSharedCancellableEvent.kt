package ru.astrainteractive.aspekt.module.adminprivate.event

import org.bukkit.event.Cancellable
import org.bukkit.event.Event

class BukkitSharedCancellableEvent<T>(private val instance: T) :
    SharedCancellableEvent where T : Event, T : Cancellable {
    override var isCancelled: Boolean
        get() = instance.isCancelled
        set(value) {
            instance.isCancelled = value
        }
}
