package ru.astrainteractive.aspekt.module.rtp.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.minecraft.teleport.ForgeTeleportApi
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.rtp.command.ForgeSafeLocationProvider
import ru.astrainteractive.aspekt.module.rtp.command.RtpCommandExecutor
import ru.astrainteractive.aspekt.module.rtp.command.rtp
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class RtpModule(
    coreModule: CoreModule,
    registerCommandsEventFlow: Flow<RegisterCommandsEvent>,
) {
    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            coreModule.scope.launch(Dispatchers.IO) {
                registerCommandsEventFlow
                    .first()
                    .rtp(
                        rtpCommandExecutor = RtpCommandExecutor(
                            scope = coreModule.scope,
                            messenger = coreModule.minecraftMessenger,
                            safeLocationProvider = ForgeSafeLocationProvider(),
                            teleportApi = ForgeTeleportApi(),
                            dispatchers = coreModule.dispatchers
                        )
                    )
            }
        }
    )
}
