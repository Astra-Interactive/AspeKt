package ru.astrainteractive.aspekt.module.sethome.api

import ru.astrainteractive.aspekt.module.sethome.model.PlayerHome
import ru.astrainteractive.klibs.kstorage.suspend.StateFlowSuspendMutableKrate
import java.util.UUID

internal interface PlayerHomesRepository {
    fun homesOf(uuid: UUID): StateFlowSuspendMutableKrate<List<PlayerHome>>

    fun clear()
}
