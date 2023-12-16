package ru.astrainteractive.aspekt.module.adminprivate.debounce

internal interface RetractKey {
    class Vararg(vararg val value: Any) : RetractKey
}
