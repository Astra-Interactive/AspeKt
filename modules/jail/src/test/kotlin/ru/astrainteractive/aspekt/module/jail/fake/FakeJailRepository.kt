package ru.astrainteractive.aspekt.module.jail.fake

import ru.astrainteractive.aspekt.module.jail.api.JailRepository
import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.klibs.mikro.core.coroutines.propagateCancellationException

internal class FakeJailRepository(
    jails: List<Jail>,
    inmates: List<JailInmate>
) : JailRepository {
    private val jails = jails.toMutableList()
    private val inmates = inmates.toMutableList()
    var isFreeFailing = false

    val currentInmates: List<JailInmate>
        get() = inmates.toList()

    override suspend fun deleteJail(jailName: String): Result<Unit> {
        return runCatching {
            jails.removeAll { jail -> jail.name == jailName }
            Unit
        }.propagateCancellationException()
    }

    override suspend fun addJail(jail: Jail): Result<Unit> {
        return runCatching { jails += jail }.propagateCancellationException()
    }

    override suspend fun findJails(): Result<List<Jail>> = Result.success(jails.toList())

    override suspend fun findJail(name: String): Result<Jail> {
        return runCatching { jails.first { jail -> jail.name == name } }.propagateCancellationException()
    }

    override suspend fun findJailInmates(jailName: String): Result<List<JailInmate>> {
        return Result.success(inmates.filter { inmate -> inmate.jailName == jailName })
    }

    override suspend fun findInmates(): Result<List<JailInmate>> = Result.success(inmates.toList())

    override suspend fun addInmate(inmate: JailInmate): Result<Unit> {
        return runCatching { inmates += inmate }.propagateCancellationException()
    }

    override suspend fun findInmate(uuid: String): Result<JailInmate> {
        return runCatching {
            inmates.first { inmate -> inmate.uuid == uuid }
        }.propagateCancellationException()
    }

    override suspend fun free(uuid: String): Result<Unit> {
        if (isFreeFailing) return Result.failure(IllegalStateException("The jail file is not writable"))
        inmates.removeAll { inmate -> inmate.uuid == uuid }
        return Result.success(Unit)
    }
}
