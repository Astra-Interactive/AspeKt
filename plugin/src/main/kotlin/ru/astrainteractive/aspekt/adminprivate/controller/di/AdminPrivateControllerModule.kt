package ru.astrainteractive.aspekt.adminprivate.controller.di

import ru.astrainteractive.aspekt.adminprivate.data.AdminPrivateRepository
import ru.astrainteractive.astralibs.async.KotlinDispatchers
import ru.astrainteractive.klibs.kdi.Module

interface AdminPrivateControllerModule : Module {
    val repository: AdminPrivateRepository
    val dispatchers: KotlinDispatchers
}
