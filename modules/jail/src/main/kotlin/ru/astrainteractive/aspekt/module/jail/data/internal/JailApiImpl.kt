package ru.astrainteractive.aspekt.module.jail.data.internal

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.jail.data.JailApi
import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parseOrDefault
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parseOrNull
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import java.io.File

internal class JailApiImpl(
    private val folder: File,
    private val stringFormat: StringFormat
) : JailApi {
    override suspend fun deleteJail(jailName: String): Result<Unit> {
        return runCatching {
            val file = folder.resolve("$jailName.yml")
            file.delete()
        }
    }

    override suspend fun addJail(jail: Jail): Result<Unit> {
        return runCatching {
            val file = folder.resolve("${jail.name}.yml")
            stringFormat.writeIntoFile(jail, file)
        }
    }

    override suspend fun getJails(): Result<List<Jail>> {
        return runCatching {
            folder.listFiles()
                .orEmpty()
                .mapNotNull { file -> stringFormat.parseOrNull(file) }
        }
    }

    override suspend fun getJail(name: String): Result<Jail> {
        val file = folder.resolve("$name.yml")
        return stringFormat.parse(file)
    }

    override suspend fun getInmates(): Result<List<JailInmate>> {
        val file = folder.resolve("jail.inmates.yml")
        return runCatching {
            stringFormat.parseOrDefault(file = file, factory = { emptyList<JailInmate>() })
        }
    }

    override suspend fun getJailInmates(jailName: String): Result<List<JailInmate>> {
        return getInmates().map { inmates -> inmates.filter { inmate -> inmate.jailName == jailName } }
    }

    override suspend fun addInmate(inmate: JailInmate): Result<Unit> {
        return runCatching {
            val file = folder.resolve("jail.inmates.yml")
            val inmates = stringFormat.parseOrDefault(file = file, factory = { emptyList<JailInmate>() })
            stringFormat.writeIntoFile(inmates.plus(inmate), file)
        }
    }

    override suspend fun getInmate(uuid: String): Result<JailInmate> {
        val file = folder.resolve("jail.inmates.yml")
        return runCatching {
            stringFormat.parseOrDefault(file = file, factory = { emptyList<JailInmate>() })
                .first { it.uuid == uuid }
        }
    }

    override suspend fun free(uuid: String): Result<Unit> {
        val file = folder.resolve("jail.inmates.yml")
        return runCatching {
            val newInmatesList = stringFormat
                .parseOrDefault(file = file, factory = { emptyList<JailInmate>() })
                .filter { it.uuid != uuid }
            stringFormat.writeIntoFile(newInmatesList, file)
        }
    }
}
