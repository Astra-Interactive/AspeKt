package ru.astrainteractive.aspekt.module.sethome.api.api

import ru.astrainteractive.aspekt.module.sethome.api.model.PlayerHome
import ru.astrainteractive.klibs.kstorage.suspend.StateFlowSuspendMutableKrate
import java.util.UUID

internal interface PlayerHomesRepository {
    fun homesOf(uuid: UUID): StateFlowSuspendMutableKrate<List<PlayerHome>>

    fun clear()
}
