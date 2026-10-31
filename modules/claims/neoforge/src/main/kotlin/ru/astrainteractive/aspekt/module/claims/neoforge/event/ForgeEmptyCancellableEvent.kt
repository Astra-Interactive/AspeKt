package ru.astrainteractive.aspekt.module.claims.neoforge.event

import ru.astrainteractive.aspekt.module.claims.api.debounce.api.SharedCancellableEvent

internal class ForgeEmptyCancellableEvent : SharedCancellableEvent {
    override var isCancelled: Boolean = false
}
