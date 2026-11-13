package ru.astrainteractive.aspekt.module.claims.api.usecase

import kotlinx.coroutines.test.TestScope
import ru.astrainteractive.aspekt.module.claims.api.krate.KrateClaimsRepository
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimPlayer
import ru.astrainteractive.astralibs.util.YamlStringFormat
import ru.astrainteractive.klibs.mikro.core.dispatchers.DefaultKotlinDispatchers
import java.io.File
import java.util.UUID
import kotlin.random.Random

internal object ClaimTestData {
    val claimPlayer = ClaimPlayer(
        uuid = UUID.randomUUID(),
        username = "username"
    )
    val randomChunk: ClaimChunk
        get() {
            val x = Random.nextInt(0, 100)
            val z = Random.nextInt(0, 100)
            return ClaimChunk(
                x = x,
                z = z,
                worldName = UUID.randomUUID().toString(),
                flags = emptyMap(),
                chunkKey = "$x$z".toLong()
            )
        }
    private val tempFile: File
        get() = File(System.getProperty("java.io.tmpdir"))

    fun createRepository(testScope: TestScope): KrateClaimsRepository {
        return KrateClaimsRepository(
            folder = tempFile.resolve(UUID.randomUUID().toString()),
            stringFormat = YamlStringFormat(),
            dispatchers = DefaultKotlinDispatchers,
            ioScope = testScope.backgroundScope
        )
    }
}
