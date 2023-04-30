package ru.astrainteractive.aspekt.commands.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.events.sit.SitController
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.Module
import ru.astrainteractive.astralibs.Single
import ru.astrainteractive.astralibs.async.BukkitDispatchers

interface CommandsModule : Module {
    val plugin: Dependency<AspeKt>
    val translation: Dependency<PluginTranslation>
    val dispatchers: Dependency<BukkitDispatchers>
    val sitController: Single<SitController>
}
