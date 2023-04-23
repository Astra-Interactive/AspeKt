package ru.astrainteractive.aspekt.events

import ru.astrainteractive.aspekt.events.crop.AutoCropEvent
import ru.astrainteractive.aspekt.events.restrictions.RestrictionsEvent
import ru.astrainteractive.aspekt.events.sit.SitController
import ru.astrainteractive.aspekt.events.sit.SitEvent
import ru.astrainteractive.aspekt.events.sort.SortController
import ru.astrainteractive.aspekt.events.sort.SortEvent
import ru.astrainteractive.aspekt.events.tc.TCEvent
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.di.Dependency


/**
 * Handler for all your events
 */
class EventHandler(
    sitControllerDependency: Dependency<SitController>,
    sortControllerDependency: Dependency<SortController>,
    pluginConfigDep: Dependency<PluginConfiguration>,
    bukkitDispatchers: BukkitDispatchers
) {

    init {
        SitEvent(sitControllerDependency, pluginConfigDep, bukkitDispatchers)
        SortEvent(sortControllerDependency, bukkitDispatchers)
        AutoCropEvent(pluginConfigDep, bukkitDispatchers)
        TCEvent(pluginConfigDep, bukkitDispatchers)
        RestrictionsEvent(pluginConfigDep, bukkitDispatchers)
    }
}
