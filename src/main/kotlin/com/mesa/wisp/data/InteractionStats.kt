package com.mesa.wisp.data

import com.mesa.wisp.interaction.Interaction

data class PlayerInteractionStats(
    val interactions: MutableMap<String, Int> = mutableMapOf() // Interaction.name -> count
) {
    fun increment(interaction: Interaction) {
        interactions[interaction.name] = (interactions[interaction.name] ?: 0) + 1
    }

    fun getCount(interaction: Interaction): Int {
        return interactions[interaction.name] ?: 0
    }

    fun getTotalInteractions(): Int {
        return interactions.values.sum()
    }
}

data class GlobalAggregates(
    var totalInteractions: Int = 0,
    val interactionCounts: MutableMap<String, Int> = mutableMapOf(), // Interaction.name -> total count
    val topPlayersByTotal: MutableList<Pair<String, Int>> = mutableListOf(), // UUID -> total count
    val topPlayersByInteraction: MutableMap<String, MutableList<Pair<String, Int>>> = mutableMapOf() // Interaction.name -> list of (UUID, count)
)

data class InteractionStatsData(
    val players: MutableMap<String, PlayerInteractionStats> = mutableMapOf(),
    val aggregates: GlobalAggregates = GlobalAggregates(),
    val playerNames: MutableMap<String, String> = mutableMapOf() // UUID -> Username cache
)