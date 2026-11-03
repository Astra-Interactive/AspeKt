package ru.astrainteractive.aspekt.module.claims.api.model

sealed interface ClaimCommand {
    class ShowMap(
        val claimPlayer: ClaimPlayer,
        val chunk: ClaimChunk
    ) : ClaimCommand

    class Claim(
        val claimPlayer: ClaimPlayer,
        val chunk: ClaimChunk
    ) : ClaimCommand

    class UnClaim(
        val claimPlayer: ClaimPlayer,
        val chunk: ClaimChunk
    ) : ClaimCommand

    class SetFlag(
        val claimPlayer: ClaimPlayer,
        val flag: ChunkFlag,
        val value: Boolean,
        val chunk: ClaimChunk
    ) : ClaimCommand

    class AddMember(
        val owner: ClaimPlayer,
        val member: ClaimPlayer
    ) : ClaimCommand

    class RemoveMember(
        val owner: ClaimPlayer,
        val member: ClaimPlayer
    ) : ClaimCommand
}
