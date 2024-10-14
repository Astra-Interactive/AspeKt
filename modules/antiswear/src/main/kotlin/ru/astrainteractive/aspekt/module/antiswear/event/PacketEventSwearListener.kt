package ru.astrainteractive.aspekt.module.antiswear.event

import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage
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
        event ?: return
        val user = event.user ?: return
        if (event.packetType !== PacketType.Play.Client.CHAT_MESSAGE) return
        val player = Bukkit.getPlayer(user.uuid) ?: return
        if (!swearRepository.isSwearFilterEnabled(player)) return
        val chatMessage = WrapperPlayClientChatMessage(event)
        val newMessage = chatMessage.message.replace(
            regex = SwearRuRegex.SWEAR_REGEX,
            replacement = SwearRuRegex.REPLACEMENT_STRING
        )
        if (newMessage.equals(chatMessage)) return
        chatMessage.message = newMessage
    }
}
