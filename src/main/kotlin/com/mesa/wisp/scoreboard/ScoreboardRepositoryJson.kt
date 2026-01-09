package com.mesa.wisp.scoreboard

import com.mesa.wisp.interaction.Interaction
import com.mesa.wisp.storage.JsonManager
import java.util.UUID

class ScoreboardRepositoryJson(
    persistenceManager: JsonManager
) : ScoreboardRepositoryBase {

    // Get a namespaced handle for interaction data
    private val handle = persistenceManager.getNamespace(
        namespace = "interactions-scoreboard",
        clazz = InteractionStatsData::class.java,
        defaultValue = { InteractionStatsData() }
    )

    override fun incrementInteraction(playerUuid: UUID, playerName: String, interaction: Interaction) {
        handle.update {
            // Update username cache
            playerNames[playerUuid.toString()] = playerName

            val stats = players.getOrPut(playerUuid.toString()) { PlayerInteractionStats() }
            stats.increment(interaction)
            rebuildAggregates(this)
        }
    }

    override fun getInteractionCount(playerUuid: UUID, interaction: Interaction): Int {
        return handle.get().players[playerUuid.toString()]?.getCount(interaction) ?: 0
    }

    override fun getTotalInteractions(playerUuid: UUID): Int {
        return handle.get().players[playerUuid.toString()]?.getTotalInteractions() ?: 0
    }

    override fun getPlayerStats(playerUuid: UUID): Map<Interaction, Int> {
        val stats = handle.get().players[playerUuid.toString()] ?: return emptyMap()
        return Interaction.entries.associateWith { stats.getCount(it) }
    }

    override fun getGlobalInteractionCount(interaction: Interaction): Int {
        return handle.get().aggregates.interactionCounts[interaction.name] ?: 0
    }

    override fun getGlobalTotalInteractions(): Int {
        return handle.get().aggregates.totalInteractions
    }

    override fun getGlobalStats(): Map<Interaction, Int> {
        val data = handle.get()
        return Interaction.entries.associateWith {
            data.aggregates.interactionCounts[it.name] ?: 0
        }
    }

    override fun getTopPlayersForInteraction(interaction: Interaction, limit: Int): List<Triple<UUID, String, Int>> {
        val data = handle.get()
        return data.aggregates.topPlayersByInteraction[interaction.name]
            ?.take(limit)
            ?.map { (uuidStr, count) ->
                val uuid = UUID.fromString(uuidStr)
                val name = data.playerNames[uuidStr] ?: uuidStr.substring(0, 8)
                Triple(uuid, name, count)
            }
            ?: emptyList()
    }

    override fun getTopPlayersByTotal(limit: Int): List<Triple<UUID, String, Int>> {
        val data = handle.get()
        return data.aggregates.topPlayersByTotal
            .take(limit)
            .map { (uuidStr, count) ->
                val uuid = UUID.fromString(uuidStr)
                val name = data.playerNames[uuidStr] ?: uuidStr.substring(0, 8)
                Triple(uuid, name, count)
            }
    }

    override fun getPlayerName(playerUuid: UUID): String? {
        return handle.get().playerNames[playerUuid.toString()]
    }

    private fun rebuildAggregates(data: InteractionStatsData) {
        data.aggregates.totalInteractions = 0
        data.aggregates.interactionCounts.clear()
        data.aggregates.topPlayersByTotal.clear()
        data.aggregates.topPlayersByInteraction.clear()

        data.players.forEach { (_, stats) ->
            data.aggregates.totalInteractions += stats.getTotalInteractions()
            stats.interactions.forEach { (interactionName, count) ->
                data.aggregates.interactionCounts[interactionName] =
                    (data.aggregates.interactionCounts[interactionName] ?: 0) + count
            }
        }

        data.aggregates.topPlayersByTotal.addAll(
            data.players.entries
                .map { it.key to it.value.getTotalInteractions() }
                .filter { it.second > 0 }
                .sortedByDescending { it.second }
                .take(10)
        )

        Interaction.entries.forEach { interaction ->
            val topPlayers = data.players.entries
                .map { it.key to it.value.getCount(interaction) }
                .filter { it.second > 0 }
                .sortedByDescending { it.second }
                .take(10)

            if (topPlayers.isNotEmpty()) {
                data.aggregates.topPlayersByInteraction[interaction.name] = topPlayers.toMutableList()
            }
        }
    }
}