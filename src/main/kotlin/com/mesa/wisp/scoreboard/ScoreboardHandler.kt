package com.mesa.wisp.scoreboard

import com.mesa.wisp.config.ConfigService
import com.mesa.wisp.config.MessageKey
import com.mesa.wisp.interaction.Interaction
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.slf4j.LoggerFactory
import java.util.*

object ScoreboardHandler {
    private val logger = LoggerFactory.getLogger("wisp")
    private lateinit var configService: ConfigService
    private lateinit var scoreboardService: ScoreboardService

    fun init(
        configService: ConfigService,
        scoreboardService: ScoreboardService
    ) {
        this.configService = configService
        this.scoreboardService = scoreboardService
    }

    /**
     * Show a player's interaction statistics
     */
    fun showPlayerStats(ctx: CommandContext<ServerCommandSource>, player: ServerPlayerEntity?): Int {
        val target = player ?: run {
            ctx.source.sendError(configService.getMessageText(MessageKey.SCOREBOARD_PLAYER_NOT_FOUND))
            return 0
        }

        val stats = scoreboardService.getPlayerStats(target.uuid)
        val total = scoreboardService.getTotalInteractions(target.uuid)

        // Header
        ctx.source.sendFeedback({
            configService.getMessageText(
                MessageKey.SCOREBOARD_PLAYER_STATS_HEADER,
                mapOf("player" to target.name.string)
            )
        }, false)

        // Total
        ctx.source.sendFeedback({
            configService.getMessageText(
                MessageKey.SCOREBOARD_PLAYER_STATS_TOTAL,
                mapOf("total" to total.toString())
            )
        }, false)

        if (stats.isNotEmpty()) {
            // Breakdown header
            ctx.source.sendFeedback({
                configService.getMessageText(MessageKey.SCOREBOARD_PLAYER_STATS_BREAKDOWN)
            }, false)

            stats.entries
                .sortedByDescending { it.value }
                .forEach { (interaction, count) ->
                    if (count > 0) {
                        ctx.source.sendFeedback({
                            configService.getMessageText(
                                MessageKey.SCOREBOARD_PLAYER_STATS_ITEM,
                                mapOf(
                                    "interaction" to interaction.plural,
                                    "count" to count.toString()
                                )
                            )
                        }, false)
                    }
                }
        } else {
            ctx.source.sendFeedback({
                configService.getMessageText(MessageKey.SCOREBOARD_NO_INTERACTIONS)
            }, false)
        }

        return 1
    }

    /**
     * Show top players by total interactions
     */
    fun showTopPlayers(ctx: CommandContext<ServerCommandSource>, limit: Int): Int {
        val topPlayers = scoreboardService.getTopPlayersByTotal(limit)

        // Header
        ctx.source.sendFeedback({
            configService.getMessageText(
                MessageKey.SCOREBOARD_TOP_PLAYERS_HEADER,
                mapOf("limit" to limit.toString())
            )
        }, false)

        if (topPlayers.isEmpty()) {
            ctx.source.sendFeedback({
                configService.getMessageText(MessageKey.SCOREBOARD_NO_DATA)
            }, false)
            return 1
        }

        topPlayers.forEachIndexed { index, (uuid, name, count) ->
            val rank = "${index + 1}."

            ctx.source.sendFeedback({
                configService.getMessageText(
                    MessageKey.SCOREBOARD_TOP_PLAYERS_ITEM,
                    mapOf(
                        "rank" to rank,
                        "player" to name,
                        "count" to count.toString()
                    )
                )
            }, false)
        }

        return 1
    }

    /**
     * Show top players for a specific interaction type
     */
    fun showTopForInteraction(ctx: CommandContext<ServerCommandSource>, interactionName: String, limit: Int): Int {
        val interaction = try {
            Interaction.valueOf(interactionName.uppercase())
        } catch (e: IllegalArgumentException) {
            ctx.source.sendError(
                configService.getMessageText(
                    MessageKey.SCOREBOARD_UNKNOWN_INTERACTION,
                    mapOf("interaction" to interactionName)
                )
            )
            return 0
        }

        val topPlayers = scoreboardService.getTopPlayersForInteraction(interaction, limit)

        // Header
        ctx.source.sendFeedback({
            configService.getMessageText(
                MessageKey.SCOREBOARD_TOP_INTERACTION_HEADER,
                mapOf(
                    "limit" to limit.toString(),
                    "interaction" to interaction.plural
                )
            )
        }, false)

        if (topPlayers.isEmpty()) {
            ctx.source.sendFeedback({
                configService.getMessageText(MessageKey.SCOREBOARD_NO_DATA)
            }, false)
            return 1
        }

        topPlayers.forEachIndexed { index, (uuid, name, count) ->
            val rank = "${index + 1}."

            ctx.source.sendFeedback({
                configService.getMessageText(
                    MessageKey.SCOREBOARD_TOP_INTERACTION_ITEM,
                    mapOf(
                        "rank" to rank,
                        "player" to name,
                        "count" to count.toString()
                    )
                )
            }, false)
        }

        return 1
    }

    /**
     * Show global interaction statistics
     */
    fun showGlobalStats(ctx: CommandContext<ServerCommandSource>): Int {
        val globalStats = scoreboardService.getGlobalStats()
        val total = scoreboardService.getGlobalTotalInteractions()

        // Header
        ctx.source.sendFeedback({
            configService.getMessageText(MessageKey.SCOREBOARD_GLOBAL_STATS_HEADER)
        }, false)

        // Total
        ctx.source.sendFeedback({
            configService.getMessageText(
                MessageKey.SCOREBOARD_GLOBAL_STATS_TOTAL,
                mapOf("total" to total.toString())
            )
        }, false)

        if (globalStats.isNotEmpty()) {
            // Breakdown header
            ctx.source.sendFeedback({
                configService.getMessageText(MessageKey.SCOREBOARD_GLOBAL_STATS_BREAKDOWN)
            }, false)

            globalStats.entries
                .sortedByDescending { it.value }
                .forEach { (interaction, count) ->
                    if (count > 0) {
                        val percentage = if (total > 0) {
                            String.format("%.1f%%", (count.toDouble() / total * 100))
                        } else "0%"

                        ctx.source.sendFeedback({
                            configService.getMessageText(
                                MessageKey.SCOREBOARD_GLOBAL_STATS_ITEM,
                                mapOf(
                                    "interaction" to interaction.plural,
                                    "count" to count.toString(),
                                    "percentage" to percentage
                                )
                            )
                        }, false)
                    }
                }
        } else {
            ctx.source.sendFeedback({
                configService.getMessageText(MessageKey.SCOREBOARD_NO_INTERACTIONS)
            }, false)
        }

        return 1
    }
}