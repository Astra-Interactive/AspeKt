package ru.astrainteractive.aspekt.invisibleframes.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.invisibleframes.event.InvisibleFramesEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File

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

    companion object {
        fun getConfigurationFile(dataFolder: File): File = dataFolder.resolve("invisible_frames.yml")
    }
}
