package ru.astrainteractive.aspekt.module.auth.api

import ru.astrainteractive.aspekt.module.auth.model.AuthState
import ru.astrainteractive.aspekt.module.auth.model.PlayerLogin
import java.util.UUID

interface AuthorizedApi {

    fun resolveAuthState(uuid: UUID): AuthState

    fun loadUserInfo(player: PlayerLogin)

    fun authUser(uuid: UUID)

    fun forgetUser(uuid: UUID)
}
