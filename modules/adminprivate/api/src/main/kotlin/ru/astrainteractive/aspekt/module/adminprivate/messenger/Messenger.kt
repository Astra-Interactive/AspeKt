package ru.astrainteractive.aspekt.module.adminprivate.messenger

import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimPlayer
import ru.astrainteractive.astralibs.string.StringDesc

interface Messenger {
    fun sendMessage(player: ClaimPlayer, stringDesc: StringDesc)
}
