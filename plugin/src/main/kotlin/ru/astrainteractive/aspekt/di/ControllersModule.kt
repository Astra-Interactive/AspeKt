package ru.astrainteractive.aspekt.di

import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.event.sort.SortController
import ru.astrainteractive.klibs.kdi.Module

interface ControllersModule : Module {
    val sitController: SitController
    val sortControllers: SortController
}
