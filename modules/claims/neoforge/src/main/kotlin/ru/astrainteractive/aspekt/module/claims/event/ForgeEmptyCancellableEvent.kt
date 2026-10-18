package ru.astrainteractive.aspekt.module.claims.event

import ru.astrainteractive.aspekt.module.claims.debounce.api.SharedCancellableEvent

internal class ForgeEmptyCancellableEvent : SharedCancellableEvent {
    override var isCancelled: Boolean = false
}
