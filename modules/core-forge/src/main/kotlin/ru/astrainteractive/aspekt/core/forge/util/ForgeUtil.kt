package ru.astrainteractive.aspekt.core.forge.util

import com.mojang.authlib.GameProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.fml.loading.FMLLoader
import ru.astrainteractive.aspekt.core.forge.event.flowEvent
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import java.util.UUID

object ForgeUtil : Logger by JUtiltLogger("AspeKt-ForgeUtil") {
    private val serverFlow = flowEvent<ServerStartedEvent>()
        .map { event -> event.server }
        .flowOn(Dispatchers.IO)
        .stateIn(GlobalScope, SharingStarted.Eagerly, null)

    internal val serverOrNull: MinecraftServer?
        get() {
            val server = serverFlow.value
            if (server == null) {
                error { "#serverOrNull could not get server!" }
            }
            return server
        }

    fun initialize() = Unit
}

fun ForgeUtil.isModLoaded(modId: String): Boolean {
    return FMLLoader.getLoadingModList().getModFileById(modId) != null
}

fun ForgeUtil.getDefaultWorldName(): String? {
    return serverOrNull
        ?.getLevel(Level.OVERWORLD)
        ?.level
        ?.dimension()
        ?.location()
        ?.path
}

fun ForgeUtil.getPlayerGameProfile(uuid: UUID): GameProfile? {
    return serverOrNull?.profileCache?.get(uuid)?.orElse(null)
}

fun ForgeUtil.getPlayerGameProfile(name: String): GameProfile? {
    return serverOrNull?.profileCache?.get(name)?.orElse(null)
}

fun ForgeUtil.getOnlinePlayers(): List<ServerPlayer> {
    return serverOrNull
        ?.playerList
        ?.players
        ?.toList()
        .orEmpty()
        .filterNotNull()
}

fun ForgeUtil.getOnlinePlayer(uuid: UUID): ServerPlayer? {
    return serverOrNull?.playerList?.getPlayer(uuid)
}

fun ForgeUtil.getOnlinePlayer(name: String): ServerPlayer? {
    return serverOrNull?.playerList?.getPlayerByName(name)
}
