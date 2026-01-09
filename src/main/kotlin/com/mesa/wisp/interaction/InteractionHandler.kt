package com.mesa.wisp.interaction

import com.mesa.wisp.service.InteractionScoreboardService
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.slf4j.LoggerFactory
import java.util.Locale.getDefault

object InteractionHandler {
    private val logger = LoggerFactory.getLogger("wisp")
    private lateinit var scoreboardService: InteractionScoreboardService

    fun init(scoreboardService: InteractionScoreboardService) {
        this.scoreboardService = scoreboardService
    }

    fun handle(
        ctx: CommandContext<ServerCommandSource>,
        interaction: Interaction
    ): Int {
        val sender = ctx.source.player

        if (sender === null) {
            logger.error("wth")
            return 1
        }

        val target = EntityArgumentType.getPlayer(ctx, "target")

        logger.info("count up!")
        scoreboardService.incrementInteraction(
            sender.uuid,
            sender.name.string,
            interaction)

        if (sender.uuid == target.uuid) {
            ctx.source.server.playerManager.broadcast(
                Text.literal("You ${interaction.verbPast} yourself!"),
                false)

            return 1
        }

        sender.sendMessage(
            Text.literal("you ${interaction.verbPast} ${target?.name?.string}!")
        )

        target.sendMessage(
            Text.literal(
                "${sender?.name?.string} ${interaction.verbPast} you!"
            )
        )

        logger.info("${sender?.name?.string} used /${interaction.name.lowercase(getDefault())} on ${target.name.string}")

        return 1
    }
}
