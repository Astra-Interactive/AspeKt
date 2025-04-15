package ru.astrainteractive.aspekt.module.claims.event

class ForgeEmptyCancellableEvent : SharedCancellableEvent {
    override var isCancelled: Boolean = false
}
