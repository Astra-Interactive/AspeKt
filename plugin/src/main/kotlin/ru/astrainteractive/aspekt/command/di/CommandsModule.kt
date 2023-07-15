package ru.astrainteractive.aspekt.command.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.klibs.kdi.Module

interface CommandsModule : Module {
    val plugin: AspeKt
    val translation: PluginTranslation
    val dispatchers: BukkitDispatchers
    val sitController: SitController
}
