package com.mesa.wisp.scoreboard

import com.mesa.wisp.interaction.Interaction
import java.util.UUID

/**
 * Service for managing interaction statistics and leaderboards
 * Delegates all storage operations to the InteractionStorage implementation
 */
class ScoreboardService(
    private val repository: ScoreboardRepositoryBase
) {
    fun incrementInteraction(playerUuid: UUID, playerName: String, interaction: Interaction) {
        repository.incrementInteraction(playerUuid, playerName, interaction)
    }

    fun getInteractionCount(playerUuid: UUID, interaction: Interaction): Int {
        return repository.getInteractionCount(playerUuid, interaction)
    }

    fun getTotalInteractions(playerUuid: UUID): Int {
        return repository.getTotalInteractions(playerUuid)
    }

    fun getPlayerStats(playerUuid: UUID): Map<Interaction, Int> {
        return repository.getPlayerStats(playerUuid)
    }

    fun getGlobalInteractionCount(interaction: Interaction): Int {
        return repository.getGlobalInteractionCount(interaction)
    }

    fun getGlobalTotalInteractions(): Int {
        return repository.getGlobalTotalInteractions()
    }

    fun getGlobalStats(): Map<Interaction, Int> {
        return repository.getGlobalStats()
    }

    fun getTopPlayersForInteraction(interaction: Interaction, limit: Int = 10): List<Triple<UUID, String, Int>> {
        return repository.getTopPlayersForInteraction(interaction, limit)
    }

    fun getTopPlayersByTotal(limit: Int = 10): List<Triple<UUID, String, Int>> {
        return repository.getTopPlayersByTotal(limit)
    }

    fun getPlayerName(playerUuid: UUID): String? {
        return repository.getPlayerName(playerUuid)
    }
}