package com.mesa.wisp.scoreboard

import com.mesa.wisp.interaction.Interaction
import java.util.UUID

interface ScoreboardRepositoryBase {
    fun incrementInteraction(playerUuid: UUID, playerName: String, interaction: Interaction)
    fun getInteractionCount(playerUuid: UUID, interaction: Interaction): Int
    fun getTotalInteractions(playerUuid: UUID): Int
    fun getPlayerStats(playerUuid: UUID): Map<Interaction, Int>
    fun getGlobalInteractionCount(interaction: Interaction): Int
    fun getGlobalTotalInteractions(): Int
    fun getGlobalStats(): Map<Interaction, Int>
    fun getTopPlayersForInteraction(interaction: Interaction, limit: Int = 10): List<Triple<UUID, String, Int>>
    fun getTopPlayersByTotal(limit: Int = 10): List<Triple<UUID, String, Int>>
    fun getPlayerName(playerUuid: UUID): String?
}