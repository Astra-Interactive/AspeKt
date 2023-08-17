package ru.astrainteractive.aspekt.adminprivate.controller.di

import ru.astrainteractive.aspekt.adminprivate.data.AdminPrivateRepository
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface AdminPrivateControllerModule : Module {
    val repository: AdminPrivateRepository
    val dispatchers: KotlinDispatchers
}
