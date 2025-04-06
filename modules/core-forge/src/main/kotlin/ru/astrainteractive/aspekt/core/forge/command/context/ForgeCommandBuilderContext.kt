package ru.astrainteractive.aspekt.core.forge.command.context

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import ru.astrainteractive.astralibs.command.api.error.ErrorHandler

class ForgeCommandBuilderContext(
    val builder: LiteralArgumentBuilder<CommandSourceStack>,
    val errorHandler: ErrorHandler<ForgeCommandContext>
)
