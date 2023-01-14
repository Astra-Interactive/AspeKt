package ru.astrainteractive.astratemplate.modules

import com.astrainteractive.astratemplate.domain.Repository
import com.astrainteractive.astratemplate.domain.local.entities.RatingRelationTable
import com.astrainteractive.astratemplate.domain.local.entities.UserTable
import com.astrainteractive.astratemplate.domain.remote.RestApi
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.com.google.gson.Gson
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.EmpireSerializer
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.di.reloadable
import ru.astrainteractive.astralibs.di.value
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.rest.RestRequester
import ru.astrainteractive.astralibs.utils.toClass
import ru.astrainteractive.astratemplate.api.ItemStackSpigotAPI
import ru.astrainteractive.astratemplate.events.EventHandler
import ru.astrainteractive.astratemplate.gui.SampleGUIViewModel
import ru.astrainteractive.astratemplate.utils.Files
import ru.astrainteractive.astratemplate.utils.PluginConfig
import ru.astrainteractive.astratemplate.utils.PluginTranslation
import java.io.File

val PluginConfigModule = reloadable {
    EmpireSerializer.toClass<PluginConfig>(Files.configFile) ?: PluginConfig()
}
val TranslationModule = reloadable {
    PluginTranslation()
}
val RestRequesterModule = module {
    RestRequester {
        this.baseUrl = "https://rickandmortyapi.com/"
        this.converterFactory = { json, clazz ->
            json?.let { Gson().fromJson(json, clazz) }
        }
        this.decoderFactory = Gson()::toJson
    }
}

val RestApiModule = module {
    val restRequestor by RestRequesterModule
    RestRequesterModule.value.create(RestApi::class.java)
}

val SQLDatabaseModule = module {
    runBlocking {
        val database = Database()
        database.openConnection("${AstraLibs.instance.dataFolder}${File.separator}data.db", DBConnection.SQLite)
        UserTable.create(database)
        RatingRelationTable.create(database)
        database
    }
}

val RepositoryModule = module {
    val restApi by RestApiModule
    val sqlDatabase by SQLDatabaseModule
    Repository(sqlDatabase, restApi)
}

val SampleGuiViewModelFactory = value {
    val repository by RepositoryModule
    SampleGUIViewModel(repository, ItemStackSpigotAPI)
}
val eventHandlerModule = module {
    EventHandler()
}