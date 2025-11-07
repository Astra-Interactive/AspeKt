package ru.astrainteractive.aspekt.module.claims.command.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandRegistrar
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimErrorMapper
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

internal class ClaimCommandModule(
    private val coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule,
    private val claimsRepository: ClaimsRepository,
    private val claimErrorMapper: ClaimErrorMapper
) {

    private val executor = ClaimCommandExecutor(
        scope = coreModule.ioScope,
        dispatchers = coreModule.dispatchers,
        translationKrate = coreModule.translation,
        kyoriKrate = coreModule.kyoriKrate,
        claimsRepository = claimsRepository,
        claimErrorMapper = claimErrorMapper,
        minecraftNativeBridge = coreModule.minecraftNativeBridge,
        platformServer = coreModule.platformServer
    )

    private val nodes = buildList {
        ClaimCommandRegistrar(
            executor = executor,
            translationKrate = coreModule.translation,
            kyoriKrate = coreModule.kyoriKrate,
        ).createNode().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(bukkitCoreModule.commandRegistrarContext::registerWhenReady)
        }
    )
}
