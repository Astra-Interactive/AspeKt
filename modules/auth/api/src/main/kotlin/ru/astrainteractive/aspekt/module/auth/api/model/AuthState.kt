package ru.astrainteractive.aspekt.module.auth.api.model

sealed interface AuthState {
    data object NotAuthorized : AuthState
    data object NotRegistered : AuthState
    data object Authorized : AuthState
    data object Pending : AuthState
}
