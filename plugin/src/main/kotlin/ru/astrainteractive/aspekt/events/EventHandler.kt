package ru.astrainteractive.aspekt.events

import ru.astrainteractive.aspekt.events.crop.AutoCrop
import ru.astrainteractive.aspekt.events.sit.SitController
import ru.astrainteractive.aspekt.events.sit.SitEvent
import ru.astrainteractive.aspekt.events.sort.SortController
import ru.astrainteractive.aspekt.events.sort.SortEvent
import ru.astrainteractive.aspekt.events.tc.TCEvent
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
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
