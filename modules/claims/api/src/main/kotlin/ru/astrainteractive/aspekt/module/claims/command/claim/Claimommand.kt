package ru.astrainteractive.aspekt.module.claims.command.claim

import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.astralibs.command.api.exception.CommandException

interface Claimommand {
    sealed interface Model {
        class ShowMap(
            val claimPlayer: ClaimPlayer,
            val chunk: ClaimChunk
        ) : Model

        class Claim(
            val claimPlayer: ClaimPlayer,
            val chunk: ClaimChunk
        ) : Model

        class UnClaim(
            val claimPlayer: ClaimPlayer,
            val chunk: ClaimChunk
        ) : Model

        class SetFlag(
            val claimPlayer: ClaimPlayer,
            val flag: ChunkFlag,
            val value: Boolean,
            val chunk: ClaimChunk
        ) : Model
    }

    sealed class Error(message: String) : CommandException(message) {
        data object NotPlayer : Error("The sender is not a player")
    }
}
