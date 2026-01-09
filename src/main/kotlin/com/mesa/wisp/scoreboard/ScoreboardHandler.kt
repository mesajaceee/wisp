package com.mesa.wisp.scoreboard

import com.mesa.wisp.interaction.Interaction
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.slf4j.LoggerFactory
import java.util.*

object ScoreboardHandler {
    private val logger = LoggerFactory.getLogger("wisp")
    private lateinit var scoreboardService: ScoreboardService

    fun init(service: ScoreboardService) {
        scoreboardService = service
    }

    /**
     * Show a player's interaction statistics
     */
    fun showPlayerStats(ctx: CommandContext<ServerCommandSource>, player: ServerPlayerEntity?): Int {
        val target = player ?: run {
            ctx.source.sendError(Text.literal("Player not found"))
            return 0
        }

        val stats = scoreboardService.getPlayerStats(target.uuid)
        val total = scoreboardService.getTotalInteractions(target.uuid)

        ctx.source.sendFeedback({
            Text.literal("=== Stats for ${target.name.string} ===")
                .formatted(Formatting.GOLD, Formatting.BOLD)
        }, false)

        ctx.source.sendFeedback({
            Text.literal("Total Interactions: $total")
                .formatted(Formatting.YELLOW)
        }, false)

        if (stats.isNotEmpty()) {
            ctx.source.sendFeedback({
                Text.literal("Breakdown:")
                    .formatted(Formatting.GRAY)
            }, false)

            stats.entries
                .sortedByDescending { it.value }
                .forEach { (interaction, count) ->
                    if (count > 0) {
                        ctx.source.sendFeedback({
                            Text.literal("  ${interaction.plural}: $count")
                                .formatted(Formatting.WHITE)
                        }, false)
                    }
                }
        } else {
            ctx.source.sendFeedback({
                Text.literal("No interactions yet!")
                    .formatted(Formatting.GRAY, Formatting.ITALIC)
            }, false)
        }

        return 1
    }

    /**
     * Show top players by total interactions
     */
    fun showTopPlayers(ctx: CommandContext<ServerCommandSource>, limit: Int): Int {
        val topPlayers = scoreboardService.getTopPlayersByTotal(limit)

        ctx.source.sendFeedback({
            Text.literal("=== Top $limit Players (Total Interactions) ===")
                .formatted(Formatting.GOLD, Formatting.BOLD)
        }, false)

        if (topPlayers.isEmpty()) {
            ctx.source.sendFeedback({
                Text.literal("No data yet!")
                    .formatted(Formatting.GRAY, Formatting.ITALIC)
            }, false)
            return 1
        }

        topPlayers.forEachIndexed { index, (uuid, name, count) ->
            val medal = when (index) {
                0 -> "🥇"
                1 -> "🥈"
                2 -> "🥉"
                else -> "${index + 1}."
            }

            ctx.source.sendFeedback({
                Text.literal("$medal $name: $count")  // Use cached name!
                    .formatted(if (index < 3) Formatting.YELLOW else Formatting.WHITE)
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
            ctx.source.sendError(Text.literal("Unknown interaction: $interactionName"))
            return 0
        }

        val topPlayers = scoreboardService.getTopPlayersForInteraction(interaction, limit)

        ctx.source.sendFeedback({
            Text.literal("=== Top $limit Players (${interaction.plural}) ===")
                .formatted(Formatting.GOLD, Formatting.BOLD)
        }, false)

        if (topPlayers.isEmpty()) {
            ctx.source.sendFeedback({
                Text.literal("No data yet!")
                    .formatted(Formatting.GRAY, Formatting.ITALIC)
            }, false)
            return 1
        }

        topPlayers.forEachIndexed { index, (uuid, name, count) ->
            val medal = when (index) {
                0 -> "🥇"
                1 -> "🥈"
                2 -> "🥉"
                else -> "${index + 1}."
            }

            ctx.source.sendFeedback({
                Text.literal("$medal $name: $count")  // Use cached name!
                    .formatted(if (index < 3) Formatting.YELLOW else Formatting.WHITE)
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

        ctx.source.sendFeedback({
            Text.literal("=== Global Interaction Statistics ===")
                .formatted(Formatting.AQUA, Formatting.BOLD)
        }, false)

        ctx.source.sendFeedback({
            Text.literal("Total Interactions: $total")
                .formatted(Formatting.GRAY)
        }, false)

        if (globalStats.isNotEmpty()) {
            ctx.source.sendFeedback({
                Text.literal("Breakdown:")
                    .formatted(Formatting.GRAY)
            }, false)

            globalStats.entries
                .sortedByDescending { it.value }
                .forEach { (interaction, count) ->
                    if (count > 0) {
                        val percentage = if (total > 0) {
                            String.format("%.1f%%", (count.toDouble() / total * 100))
                        } else "0%"

                        ctx.source.sendFeedback({
                            Text.literal("  ${interaction.plural}: $count ($percentage)")
                                .formatted(Formatting.WHITE)
                        }, false)
                    }
                }
        } else {
            ctx.source.sendFeedback({
                Text.literal("No interactions yet!")
                    .formatted(Formatting.GRAY, Formatting.ITALIC)
            }, false)
        }

        return 1
    }

    /**
     * Helper to get player name from UUID
     */
    private fun getPlayerName(ctx: CommandContext<ServerCommandSource>, uuid: UUID): String {
        // Try cache first
        val cachedName = scoreboardService.getPlayerName(uuid)
        if (cachedName != null) return cachedName

        // Try online player
        val player = ctx.source.server.playerManager.getPlayer(uuid)
        if (player != null) return player.name.string

        // Fallback to short UUID
        return uuid.toString().substring(0, 8)
    }
}