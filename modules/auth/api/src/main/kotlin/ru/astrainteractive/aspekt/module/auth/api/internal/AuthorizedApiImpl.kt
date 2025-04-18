package ru.astrainteractive.aspekt.module.auth.api.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.auth.api.AuthDao
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.model.PlayerLoginModel
import java.util.UUID

internal class AuthorizedApiImpl(
    private val authDao: AuthDao,
    private val scope: CoroutineScope
) : AuthorizedApi {
    private val authorizedMap = HashMap<UUID, AuthorizedApi.AuthState>()

    override fun getAuthState(uuid: UUID): AuthorizedApi.AuthState {
        return authorizedMap.getOrDefault(uuid, AuthorizedApi.AuthState.Pending)
    }

    override fun loadUserInfo(player: PlayerLoginModel) {
        scope.launch {
            authDao.getUser(player.uuid)
                .onFailure {
                    authorizedMap.put(player.uuid, AuthorizedApi.AuthState.NotRegistered)
                }.onSuccess { user ->
                    if (user.lastIpAddress == player.ip) {
                        authorizedMap.put(player.uuid, AuthorizedApi.AuthState.Authorized)
                    } else {
                        authorizedMap.put(player.uuid, AuthorizedApi.AuthState.NotAuthorized)
                    }
                }
        }
    }

    override fun authUser(uuid: UUID) {
        authorizedMap.put(uuid, AuthorizedApi.AuthState.Authorized)
    }

    override fun forgetUser(uuid: UUID) {
        authorizedMap.remove(uuid)
    }
}
