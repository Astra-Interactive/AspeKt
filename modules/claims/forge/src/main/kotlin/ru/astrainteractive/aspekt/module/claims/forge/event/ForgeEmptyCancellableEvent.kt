package ru.astrainteractive.aspekt.module.claims.forge.event

import ru.astrainteractive.aspekt.module.claims.api.debounce.api.SharedCancellableEvent

internal class ForgeEmptyCancellableEvent : SharedCancellableEvent {
    override var isCancelled: Boolean = false
}
