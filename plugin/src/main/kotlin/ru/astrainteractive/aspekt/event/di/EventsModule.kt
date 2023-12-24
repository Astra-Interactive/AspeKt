package ru.astrainteractive.aspekt.event.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.crop.AutoCropEvent
import ru.astrainteractive.aspekt.event.crop.di.AutoCropDependencies
import ru.astrainteractive.aspekt.event.moneydrop.di.MoneyDropModule
import ru.astrainteractive.aspekt.event.restrictions.RestrictionsEvent
import ru.astrainteractive.aspekt.event.restrictions.di.RestrictionsDependencies
import ru.astrainteractive.aspekt.event.sit.di.SitModule
import ru.astrainteractive.aspekt.event.sort.SortEvent
import ru.astrainteractive.aspekt.event.sort.di.SortDependencies
import ru.astrainteractive.aspekt.event.tc.TCEvent
import ru.astrainteractive.aspekt.event.tc.di.TCDependencies
import ru.astrainteractive.klibs.kdi.Module

interface EventsModule : Module {
    val tcEvent: TCEvent
    val sortEvent: SortEvent
    val sitModule: SitModule
    val restrictionsEvent: RestrictionsEvent
    val autoCropEvent: AutoCropEvent
    val moneyDropModule: MoneyDropModule

    class Default(coreModule: CoreModule) : EventsModule {

        override val tcEvent: TCEvent by lazy {
            val tcDependencies: TCDependencies = TCDependencies.Default(coreModule)
            TCEvent(tcDependencies)
        }

        override val sortEvent: SortEvent by lazy {
            val sortDependencies: SortDependencies = SortDependencies.Default(coreModule)
            SortEvent(sortDependencies)
        }

        override val sitModule: SitModule by lazy {
            SitModule.Default(coreModule)
        }

        override val restrictionsEvent: RestrictionsEvent by lazy {
            val restrictionsDependencies: RestrictionsDependencies = RestrictionsDependencies.Default(coreModule)
            RestrictionsEvent(restrictionsDependencies)
        }

        override val autoCropEvent: AutoCropEvent by lazy {
            val autoCropDependencies: AutoCropDependencies = AutoCropDependencies.Default(coreModule)
            AutoCropEvent(autoCropDependencies)
        }

        override val moneyDropModule: MoneyDropModule by lazy {
            MoneyDropModule.Default(coreModule)
        }
    }
}
