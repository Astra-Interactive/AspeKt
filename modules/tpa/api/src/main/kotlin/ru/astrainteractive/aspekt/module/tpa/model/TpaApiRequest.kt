package ru.astrainteractive.aspekt.module.tpa.model

import java.util.UUID

data class TpaApiRequest(
    val targetUuid: UUID,
    val type: TpaApiRequestType
)
