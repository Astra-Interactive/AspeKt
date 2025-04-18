package ru.astrainteractive.aspekt.module.auth.api

import ru.astrainteractive.aspekt.module.auth.api.model.PlayerLoginModel
import java.util.UUID

interface AuthorizedApi {
    sealed interface AuthState {
        data object NotAuthorized : AuthState
        data object NotRegistered : AuthState
        data object Authorized : AuthState
        data object Pending : AuthState
    }

    /**
     * Check is user authorized or not
     */
    fun getAuthState(uuid: UUID): AuthState

    /**
     * Remember user on join so we can call [getAuthState]
     */
    fun loadUserInfo(player: PlayerLoginModel)

    fun authUser(uuid: UUID)

    /**
     * Clear [getAuthState] when user leave server
     */
    fun forgetUser(uuid: UUID)
}
