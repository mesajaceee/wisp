package com.mesa.wisp.interaction

import com.mesa.wisp.config.ConfigService
import com.mesa.wisp.config.MessageKey
import com.mesa.wisp.particle.ParticleSpawner
import com.mesa.wisp.scoreboard.ScoreboardService
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.slf4j.LoggerFactory
import java.util.Locale.getDefault

object InteractionHandler {
    private val logger = LoggerFactory.getLogger("wisp")
    private lateinit var configService: ConfigService
    private lateinit var scoreboardService: ScoreboardService
    private lateinit var particleSpawner: ParticleSpawner

    fun init(
        configService: ConfigService,
        scoreboardService: ScoreboardService,
        particleSpawner: ParticleSpawner
    ) {
        this.configService = configService
        this.scoreboardService = scoreboardService
        this.particleSpawner = particleSpawner
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

        scoreboardService.incrementInteraction(
            sender.uuid,
            sender.name.string,
            interaction)

        if (sender.uuid == target.uuid) {
            sender.sendMessage(
                configService.getMessageText(MessageKey.interactionSelfError(interaction))
            )

            return 1
        }

        sender.sendMessage(
            configService.getMessageText(
                MessageKey.interactionSenderSuccess(interaction),
                mapOf("target" to target.name.string)
            )
        )

        target.sendMessage(
            configService.getMessageText(MessageKey.interactionTargetSuccess(interaction),
                mapOf("sender" to sender.name.string)
            )
        )

        particleSpawner.spawnParticlesInPlayer(sender, target, interaction)

        logger.info("${sender.name?.string} used /${interaction.name.lowercase(getDefault())} on ${target.name.string}")

        return 1
    }
}
