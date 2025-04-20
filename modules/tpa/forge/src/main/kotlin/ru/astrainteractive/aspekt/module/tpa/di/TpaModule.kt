package ru.astrainteractive.aspekt.module.tpa.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.tpa.api.TpaApi
import ru.astrainteractive.aspekt.module.tpa.command.TpaCommandExecutor
import ru.astrainteractive.aspekt.module.tpa.command.tpa
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class TpaModule(
    coreModule: CoreModule,
    registerCommandsEventFlow: Flow<RegisterCommandsEvent>,
) {
    val tpaCommandExecutor = TpaCommandExecutor(
        translationKrate = coreModule.translation,
        tpaApi = TpaApi(),
        scope = coreModule.scope,
        kyoriKrate = coreModule.kyoriComponentSerializer
    )

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            coreModule.scope.launch(Dispatchers.IO) {
                registerCommandsEventFlow
                    .first()
                    .tpa(
                        tpaCommandExecutor = tpaCommandExecutor
                    )
            }
        }
    )
}
