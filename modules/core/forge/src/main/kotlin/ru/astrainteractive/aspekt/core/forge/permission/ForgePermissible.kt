@file:Suppress("UnusedPrivateProperty")

package ru.astrainteractive.aspekt.core.forge.permission

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.server.permission.PermissionAPI
import net.minecraftforge.server.permission.nodes.PermissionNode
import net.minecraftforge.server.permission.nodes.PermissionTypes
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.astralibs.permission.Permissible
import ru.astrainteractive.astralibs.permission.Permission
import java.util.UUID

class ForgePermissible(private val uuid: UUID) : Permissible {
    private fun Permission.asBooleanNode(): PermissionNode<Boolean> {
        return PermissionNode(
            ResourceLocation("aspekt", value),
            PermissionTypes.BOOLEAN,
            { player, uuid, ctx -> true }
        )
    }

    private fun Permission.asIntNode(): PermissionNode<Int> {
        return PermissionNode(
            ResourceLocation("aspekt", value),
            PermissionTypes.INTEGER,
            { player, uuid, ctx -> 0 }
        )
    }

    fun <T> PermissionNode<T>.getOrCreate(): PermissionNode<T> {
        return this
        val isPermissionRegistered = PermissionAPI.getRegisteredNodes().contains(this)
        if (!isPermissionRegistered) {
            PermissionAPI.getRegisteredNodes().add(this)
        }
        return this
    }

    override fun hasPermission(permission: Permission): Boolean {
        val node = permission.asBooleanNode().getOrCreate()
        val player = ForgeUtil.getOnlinePlayer(uuid) ?: return false
//        return PermissionAPI.getPermission(player, node)
        return false
    }

    override fun maxPermissionSize(permission: Permission): Int? {
        val node = permission.asIntNode().getOrCreate()
        val player = ForgeUtil.getOnlinePlayer(uuid) ?: return null
//        return PermissionAPI.getPermission(player, node)
        return 1
    }

    override fun minPermissionSize(permission: Permission): Int? {
        val node = permission.asIntNode().getOrCreate()
        val player = ForgeUtil.getOnlinePlayer(uuid) ?: return null
//        return PermissionAPI.getPermission(player, node)
        return 1
    }

    override fun permissionSizes(permission: Permission): List<Int> {
        val node = permission.asIntNode().getOrCreate()
        val player = ForgeUtil.getOnlinePlayer(uuid) ?: return emptyList()
//        return listOf(PermissionAPI.getPermission(player, node))
        return listOf(1)
    }
}
