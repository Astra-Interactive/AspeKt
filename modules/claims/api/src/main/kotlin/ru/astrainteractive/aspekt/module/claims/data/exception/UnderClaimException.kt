package ru.astrainteractive.aspekt.module.claims.data.exception

import java.util.UUID

data class UnderClaimException(val ownerUuid: UUID) : Exception("Claim already owned by $ownerUuid")
