package ru.astrainteractive.aspekt.module.claims.event

internal class ForgeEmptyCancellableEvent : SharedCancellableEvent {
    override var isCancelled: Boolean = false
}
