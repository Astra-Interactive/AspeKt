package ru.astrainteractive.aspekt.module.antiswear.event

import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepository
import ru.astrainteractive.aspekt.module.antiswear.util.SwearRuRegex
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger

internal class PacketEventSwearListener(
    private val swearRepository: SwearRepository,
) : PacketEventListener(PacketListenerPriority.NORMAL),
    Logger by JUtiltLogger("AspeKt-PacketEventSwearListener") {

    override fun onPacketReceive(event: PacketReceiveEvent?) {
        val user = event?.user ?: return
        val player = Bukkit.getPlayer(user.uuid) ?: return

        if (event.packetType == PacketType.Play.Client.CHAT_MESSAGE) {
            if (!swearRepository.isSwearFilterEnabled(player)) return
            val wrapper = WrapperPlayClientChatMessage(event)
            wrapper.message = wrapper.message.replace(
                regex = SwearRuRegex.SWEAR_REGEX,
                replacement = SwearRuRegex.REPLACEMENT_STRING
            )
        }
    }

    override fun onPacketSend(event: PacketSendEvent?) {
        val user = event?.user ?: return
        val player = Bukkit.getPlayer(user.uuid) ?: return

        when (event.packetType) {
            PacketType.Play.Server.SYSTEM_CHAT_MESSAGE -> {
                if (!swearRepository.isSwearFilterEnabled(player)) return
                val wrapper = WrapperPlayServerSystemChatMessage(event)
                wrapper.message = wrapper.message.replaceText(SwearRuRegex.REPLACEMENT_CONFIG)
            }
            PacketType.Play.Server.CHAT_MESSAGE -> {
                if (!swearRepository.isSwearFilterEnabled(player)) return
                val wrapper = WrapperPlayServerChatMessage(event)
                wrapper.message.chatContent = wrapper.message.chatContent.replaceText(SwearRuRegex.REPLACEMENT_CONFIG)
            }
        }
    }
}
