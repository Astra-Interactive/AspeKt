package ru.astrainteractive.aspekt.invisibleitemframes.di

import ru.astrainteractive.aspekt.core.di.BukkitCoreModule
import ru.astrainteractive.aspekt.invisibleitemframes.event.InvisibleFramesEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import java.io.File

class InvisibleItemFrameModule(
    bukkitCoreModule: BukkitCoreModule
) {
    private val invisibleFramesEvent: InvisibleFramesEvent = InvisibleFramesEvent()

    @Suppress("TrivialLambdaReference")
    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            invisibleFramesEvent.onEnable(bukkitCoreModule.plugin)
        },
        onDisable = {
            invisibleFramesEvent.onDisable()
        }
    )

    companion object {
        fun resolveConfigurationFile(dataFolder: File): File = dataFolder.resolve("invisible_frames.yml")
    }
}
