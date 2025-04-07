package ru.astrainteractive.aspekt.invisibleframes.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.invisibleframes.event.InvisibleFramesEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class InvisibleItemFrameModule(
    coreModule: CoreModule
) {
    private val invisibleFramesEvent: InvisibleFramesEvent = InvisibleFramesEvent()
    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            invisibleFramesEvent.onEnable(coreModule.plugin)
        },
        onDisable = {
            invisibleFramesEvent.onDisable()
        }
    )
}
