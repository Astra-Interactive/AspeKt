package ru.astrainteractive.aspekt.module.tpa.api.model

import java.util.UUID

internal data class TpaApiRequest(
    val targetUuid: UUID,
    val type: TpaApiRequestType
)
