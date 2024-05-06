package ru.astrainteractive.aspekt.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializerType

data object FixedLegacySerializer : KyoriComponentSerializer {
    override val type: KyoriComponentSerializerType = KyoriComponentSerializerType.Legacy

    override fun toComponent(string: String): Component {
        return LegacyComponentSerializer.builder()
            .extractUrls()
            .character(LegacyComponentSerializer.AMPERSAND_CHAR)
            .build()
            .deserialize(string)
            .decoration(TextDecoration.ITALIC, false)
    }
}
