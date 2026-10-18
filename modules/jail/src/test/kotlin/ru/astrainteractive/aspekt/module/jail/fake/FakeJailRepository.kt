package ru.astrainteractive.aspekt.module.jail.fake

import ru.astrainteractive.aspekt.module.jail.api.JailRepository
import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate

internal class FakeJailRepository(
    jails: List<Jail>,
    inmates: List<JailInmate>
) : JailRepository {
    private val jails = jails.toMutableList()
    private val inmates = inmates.toMutableList()
    var isFreeFailing = false

    val currentInmates: List<JailInmate>
        get() = inmates.toList()

    override suspend fun deleteJail(jailName: String): Result<Unit> = runCatching {
        jails.removeAll { jail -> jail.name == jailName }
    }

    override suspend fun addJail(jail: Jail): Result<Unit> = runCatching { jails += jail }

    override suspend fun getJails(): Result<List<Jail>> = Result.success(jails.toList())

    override suspend fun getJail(name: String): Result<Jail> = runCatching { jails.first { jail -> jail.name == name } }

    override suspend fun getJailInmates(jailName: String): Result<List<JailInmate>> {
        return Result.success(inmates.filter { inmate -> inmate.jailName == jailName })
    }

    override suspend fun getInmates(): Result<List<JailInmate>> = Result.success(inmates.toList())

    override suspend fun addInmate(inmate: JailInmate): Result<Unit> = runCatching { inmates += inmate }

    override suspend fun getInmate(uuid: String): Result<JailInmate> = runCatching {
        inmates.first { inmate -> inmate.uuid == uuid }
    }

    override suspend fun free(uuid: String): Result<Unit> {
        if (isFreeFailing) return Result.failure(IllegalStateException("The jail file is not writable"))
        inmates.removeAll { inmate -> inmate.uuid == uuid }
        return Result.success(Unit)
    }
}
