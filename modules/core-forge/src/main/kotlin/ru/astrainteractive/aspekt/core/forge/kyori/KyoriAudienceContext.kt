package ru.astrainteractive.aspekt.core.forge.kyori

import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import ru.astrainteractive.aspekt.core.forge.util.toNative
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.Krate

class KyoriAudienceContext<T>(
    val audience: T,
    val kyori: KyoriComponentSerializer
)

fun <T> KyoriComponentSerializer.wrap(instance: T): KyoriAudienceContext<T> {
    return KyoriAudienceContext(
        audience = instance,
        kyori = this
    )
}

fun <T> Krate<KyoriComponentSerializer>.withAudience(instance: T): KyoriAudienceContext<T> {
    return KyoriAudienceContext(
        audience = instance,
        kyori = cachedValue
    )
}

@JvmName("sendSystemMessageFromCommandStack")
fun KyoriAudienceContext<CommandSourceStack>.sendSystemMessage(desc: StringDesc) {
    audience.sendSystemMessage(kyori.toComponent(desc).toNative())
}

@JvmName("sendSystemMessageFromPlayer")
fun KyoriAudienceContext<out Player>.sendSystemMessage(desc: StringDesc) {
    audience.sendSystemMessage(kyori.toComponent(desc).toNative())
}
