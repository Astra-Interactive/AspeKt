package ru.astrainteractive.aspekt.core.forge.command.context

import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack

@JvmInline
value class ForgeCommandContext(
    val instance: CommandContext<CommandSourceStack>
) : ru.astrainteractive.astralibs.command.api.context.CommandContext
