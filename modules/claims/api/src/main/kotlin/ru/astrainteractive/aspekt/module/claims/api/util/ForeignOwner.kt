package ru.astrainteractive.aspekt.module.claims.api.util

import java.util.UUID

internal fun findForeignOwnerUuid(ownerUuid: UUID?, uuid: UUID): UUID? {
    if (ownerUuid == null || ownerUuid == uuid) return null
    return ownerUuid
}
