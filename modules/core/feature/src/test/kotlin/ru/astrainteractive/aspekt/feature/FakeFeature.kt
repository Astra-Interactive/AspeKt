package ru.astrainteractive.aspekt.feature

import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class FakeFeature(
    private val name: String,
    private val events: MutableList<String>
) {
    var enableCount = 0
        private set
    var disableCount = 0
        private set
    var reloadCount = 0
        private set

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            enableCount++
            events.add("$name.enable")
        },
        onDisable = {
            disableCount++
            events.add("$name.disable")
        },
        onReload = {
            reloadCount++
            events.add("$name.reload")
        }
    )
}
