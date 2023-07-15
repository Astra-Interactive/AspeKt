package ru.astrainteractive.aspekt.di.impl

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.command.di.CommandsModule
import ru.astrainteractive.aspekt.di.ControllersModule
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class CommandsModuleImpl(
    rootModule: RootModule
) : CommandsModule {
    private val controllersModule: ControllersModule by rootModule.controllersModule

    override val plugin: AspeKt by rootModule.plugin
    override val translation: PluginTranslation by rootModule.translation
    override val dispatchers: BukkitDispatchers by rootModule.dispatchers
    override val sitController: SitController by Provider { controllersModule.sitController }
}
