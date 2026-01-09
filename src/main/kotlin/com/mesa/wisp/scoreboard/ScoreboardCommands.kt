package com.mesa.wisp.scoreboard

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object ScoreboardCommands {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal("wisp")
                // /scoreboard stats - show your own stats
                .then(literal("stats")
                    .executes { ctx ->
                        ScoreboardHandler.showPlayerStats(ctx, ctx.source.player)
                    }
                    // /scoreboard stats <player> - show someone else's stats
                    .then(argument("player", EntityArgumentType.player())
                        .executes { ctx ->
                            val target = EntityArgumentType.getPlayer(ctx, "player")
                            ScoreboardHandler.showPlayerStats(ctx, target)
                        }
                    )
                )
                // /scoreboard top - show top players by total interactions
                .then(literal("top")
                    .executes { ctx ->
                        ScoreboardHandler.showTopPlayers(ctx, 10)
                    }
                    // /scoreboard top <amount>
                    .then(argument("amount", IntegerArgumentType.integer(1, 50))
                        .executes { ctx ->
                            val amount = IntegerArgumentType.getInteger(ctx, "amount")
                            ScoreboardHandler.showTopPlayers(ctx, amount)
                        }
                    )
                )
                // /scoreboard top <interaction> - show top for specific interaction
                .then(literal("top")
                    .then(argument("interaction", StringArgumentType.word())
                        .suggests { _, builder ->
                            com.mesa.wisp.interaction.Interaction.entries.forEach {
                                builder.suggest(it.name.lowercase())
                            }
                            builder.buildFuture()
                        }
                        .executes { ctx ->
                            val interactionName = StringArgumentType.getString(ctx, "interaction")
                            ScoreboardHandler.showTopForInteraction(ctx, interactionName, 10)
                        }
                        .then(argument("amount", IntegerArgumentType.integer(1, 50))
                            .executes { ctx ->
                                val interactionName = StringArgumentType.getString(ctx, "interaction")
                                val amount = IntegerArgumentType.getInteger(ctx, "amount")
                                ScoreboardHandler.showTopForInteraction(ctx, interactionName, amount)
                            }
                        )
                    )
                )
                // /scoreboard global - show global stats
                .then(literal("global")
                    .executes { ctx ->
                        ScoreboardHandler.showGlobalStats(ctx)
                    }
                )
        )
    }
}