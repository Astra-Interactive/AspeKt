@file:Suppress("UnusedPrivateProperty")

package ru.astrainteractive.aspekt.core.forge.permission

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.minecraftforge.server.permission.PermissionAPI
import net.minecraftforge.server.permission.events.PermissionGatherEvent
import net.minecraftforge.server.permission.nodes.PermissionNode
import ru.astrainteractive.aspekt.core.forge.coroutine.ForgeMainDispatcher
import ru.astrainteractive.aspekt.core.forge.event.flowEvent
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.astralibs.permission.Permissible
import ru.astrainteractive.astralibs.permission.Permission
import java.util.UUID

class ForgePermissible(private val uuid: UUID) : Permissible {
    private inline fun <reified T> Permission.asNodeOrNull(): PermissionNode<T>? {
        return PermissionAPI.getRegisteredNodes()
            .filterIsInstance<PermissionNode<T>>()
            .firstOrNull { node -> node.nodeName == this.value }
    }

    override fun hasPermission(permission: Permission): Boolean {
        val node = permission.asNodeOrNull<Boolean>() ?: return false
        val player = ForgeUtil.getOnlinePlayer(uuid) ?: return false
        return PermissionAPI.getPermission(player, node)
    }

    override fun maxPermissionSize(permission: Permission): Int? {
        val node = permission.asNodeOrNull<Int>() ?: return 0
        val player = ForgeUtil.getOnlinePlayer(uuid) ?: return null

        return PermissionAPI.getPermission(player, node)
    }

    override fun minPermissionSize(permission: Permission): Int? {
        val node = permission.asNodeOrNull<Int>() ?: return 0
        val player = ForgeUtil.getOnlinePlayer(uuid) ?: return null
        return PermissionAPI.getPermission(player, node)
    }

    override fun permissionSizes(permission: Permission): List<Int> {
        val node = permission.asNodeOrNull<Int>() ?: return emptyList()
        val player = ForgeUtil.getOnlinePlayer(uuid) ?: return emptyList()
        return listOf(PermissionAPI.getPermission(player, node))
    }

    companion object {
        /**
         * Must be executed not in onEnable, but inside init of forge entry!
         */
        fun addNodes(vararg nodes: PermissionNode<*>) {
            val scope = CoroutineScope(SupervisorJob() + ForgeMainDispatcher)
            scope.launch {
                val event = flowEvent<PermissionGatherEvent.Nodes>().first()
                event.addNodes(*nodes)
            }.invokeOnCompletion { scope.cancel() }
        }
    }
}
