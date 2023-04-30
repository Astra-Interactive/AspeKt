package ru.astrainteractive.aspekt.modules.impl

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.commands.di.CommandsModule
import ru.astrainteractive.aspekt.events.sit.SitController
import ru.astrainteractive.aspekt.modules.ControllersModule
import ru.astrainteractive.aspekt.modules.RootModule
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.Single
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.getValue

object CommandsModuleImpl : CommandsModule {
    private val rootModule: RootModule by RootModuleImpl
    private val controllersModule: ControllersModule by ControllersModuleImpl
    override val plugin: Dependency<AspeKt> = rootModule.plugin
    override val translation: Dependency<PluginTranslation> = rootModule.translation
    override val dispatchers: Dependency<BukkitDispatchers> = rootModule.dispatchers
    override val sitController: Single<SitController> = controllersModule.sitController
}
