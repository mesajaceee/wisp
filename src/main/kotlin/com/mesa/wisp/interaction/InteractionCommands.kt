package com.mesa.wisp.interaction

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object InteractionCommands {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        Interaction.entries.forEach { interaction ->
            dispatcher.register(
                literal(interaction.name.lowercase())
                    .then(argument("target", EntityArgumentType.player())
                        .executes { ctx ->
                            InteractionHandler.handle(ctx, interaction)
                        }
                    )
            )
        }
    }
}
