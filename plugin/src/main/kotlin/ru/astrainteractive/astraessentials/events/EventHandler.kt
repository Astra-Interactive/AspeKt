package ru.astrainteractive.astraessentials.events

import ru.astrainteractive.astraessentials.events.crop.AutoCrop
import ru.astrainteractive.astraessentials.events.sit.SitController
import ru.astrainteractive.astraessentials.events.sit.SitEvent
import ru.astrainteractive.astraessentials.events.sort.SortController
import ru.astrainteractive.astraessentials.events.sort.SortEvent
import ru.astrainteractive.astraessentials.events.tc.TCEvent
import ru.astrainteractive.astraessentials.modules.ServiceLocator
import ru.astrainteractive.astraessentials.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.di.Dependency


/**
 * Handler for all your events
 */
class EventHandler(
    sitControllerDependency: Dependency<SitController>,
    sortControllerDependency: Dependency<SortController>,
    pluginConfigDep: Dependency<PluginConfiguration>
) {

    init {
        SitEvent(sitControllerDependency, pluginConfigDep)
        SortEvent(sortControllerDependency)
        AutoCrop(pluginConfigDep)
        TCEvent(pluginConfigDep)
    }
}
