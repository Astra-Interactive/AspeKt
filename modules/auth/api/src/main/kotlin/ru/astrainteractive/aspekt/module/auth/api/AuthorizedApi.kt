package ru.astrainteractive.aspekt.module.auth.api

import ru.astrainteractive.aspekt.module.auth.model.AuthState
import ru.astrainteractive.aspekt.module.auth.model.PlayerLogin
import java.util.UUID

interface AuthorizedApi {

    /**
     * Check is user authorized or not
     */
    fun resolveAuthState(uuid: UUID): AuthState

    /**
     * Remember user on join so we can call [resolveAuthState]
     */
    fun loadUserInfo(player: PlayerLogin)

    fun authUser(uuid: UUID)

    /**
     * Clear [resolveAuthState] when user leave server
     */
    fun forgetUser(uuid: UUID)
}
