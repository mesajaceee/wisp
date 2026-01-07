package com.mesa.scoreboard

import com.mesa.interaction.Interaction
import net.minecraft.scoreboard.ScoreboardCriterion
import net.minecraft.server.network.ServerPlayerEntity

//object InteractionScoreboard {
//
//    fun increment(player: ServerPlayerEntity, interaction: Interaction) {
//        val server = player.server
//        val scoreboard = server.scoreboard
//
//        val objective = scoreboard.getObjective(interaction.stat)
//            ?: scoreboard.addObjective(
//                interaction.stat,
//                ScoreboardCriterion.DUMMY,
//                Text.literal(interaction.stat),
//                ScoreboardCriterion.RenderType.INTEGER
//            )
//
//        scoreboard.getPlayerScore(player.entityName, objective)
//            .incrementScore()
//    }
//}
