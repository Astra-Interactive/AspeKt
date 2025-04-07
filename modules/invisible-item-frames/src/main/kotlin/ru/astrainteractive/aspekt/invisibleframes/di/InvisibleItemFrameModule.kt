package ru.astrainteractive.aspekt.invisibleframes.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.invisibleframes.event.InvisibleFramesEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class InvisibleItemFrameModule(
    bukkitCoreModule: BukkitCoreModule
) {
    private val invisibleFramesEvent: InvisibleFramesEvent = InvisibleFramesEvent()
    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            invisibleFramesEvent.onEnable(bukkitCoreModule.plugin)
        },
        onDisable = {
            invisibleFramesEvent.onDisable()
        }
    )
}
