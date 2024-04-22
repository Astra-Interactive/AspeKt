package ru.astrainteractive.aspekt.module.newbee.event.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface EventDependencies {
    val translation: PluginTranslation
    val kyoriComponentSerializer: KyoriComponentSerializer
    val scope: CoroutineScope
    val dispatcher: KotlinDispatchers

    class Default(
        override val translation: PluginTranslation,
        override val kyoriComponentSerializer: KyoriComponentSerializer,
        override val dispatcher: KotlinDispatchers,
        override val scope: CoroutineScope
    ) : EventDependencies
}
