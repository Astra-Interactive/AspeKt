package ru.astrainteractive.aspekt

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.ComponentSerializer
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializerType
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

class UnboxedKyoriComponentSerializer(kyoriKrate: CachedKrate<KyoriComponentSerializer>) : KyoriComponentSerializer {
    private val kyori by kyoriKrate
    override val type: KyoriComponentSerializerType
        get() = kyori.type
    override val serializer: ComponentSerializer<Component, out Component, String>
        get() = kyori.serializer

    override fun toComponent(string: String): Component = kyori.toComponent(string)
}

fun CachedKrate<KyoriComponentSerializer>.asUnboxed(): KyoriComponentSerializer {
    return UnboxedKyoriComponentSerializer(this)
}
