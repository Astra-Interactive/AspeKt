package ru.astrainteractive.aspekt.module.claims.messenger

import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.astralibs.string.StringDesc

interface Messenger {
    fun sendMessage(player: ClaimPlayer, stringDesc: StringDesc)
}
