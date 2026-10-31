package ru.astrainteractive.aspekt.module.auth.api.memory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.auth.api.api.AuthStateRepository
import ru.astrainteractive.aspekt.module.auth.api.model.AuthState
import ru.astrainteractive.aspekt.module.auth.api.model.PlayerLogin
import ru.astrainteractive.aspekt.module.auth.dao.api.AuthRepository
import java.util.UUID

internal class InMemoryAuthStateRepository(
    private val authRepository: AuthRepository,
    private val scope: CoroutineScope
) : AuthStateRepository {
    private val authorizedMap = HashMap<UUID, AuthState>()

    override fun resolveAuthState(uuid: UUID): AuthState {
        return authorizedMap.getOrDefault(uuid, AuthState.Pending)
    }

    override fun loadUserInfo(player: PlayerLogin) {
        scope.launch {
            authRepository.findUser(player.uuid)
                .onFailure {
                    authorizedMap.put(player.uuid, AuthState.NotRegistered)
                }.onSuccess { user ->
                    if (user.lastIpAddress == player.ip) {
                        authorizedMap.put(player.uuid, AuthState.Authorized)
                    } else {
                        authorizedMap.put(player.uuid, AuthState.NotAuthorized)
                    }
                }
        }
    }

    override fun authUser(uuid: UUID) {
        authorizedMap.put(uuid, AuthState.Authorized)
    }

    override fun forgetUser(uuid: UUID) {
        authorizedMap.remove(uuid)
    }
}
