package ru.astrainteractive.aspekt.module.auth.api.api

import ru.astrainteractive.aspekt.module.auth.api.model.AuthState
import ru.astrainteractive.aspekt.module.auth.api.model.PlayerLogin
import java.util.UUID

interface AuthStateRepository {

    fun resolveAuthState(uuid: UUID): AuthState

    fun loadUserInfo(player: PlayerLogin)

    fun authUser(uuid: UUID)

    fun forgetUser(uuid: UUID)
}
