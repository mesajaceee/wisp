package com.mesa.wisp

import com.mesa.wisp.interaction.InteractionCommands
import com.mesa.wisp.interaction.InteractionHandler
import com.mesa.wisp.scoreboard.ScoreboardCommands
import com.mesa.wisp.scoreboard.ScoreboardHandler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import org.slf4j.LoggerFactory

object Wisp : ModInitializer {
    private val logger = LoggerFactory.getLogger("wisp")

    override fun onInitialize() {
        Services.init()

        logger.info("wisp is loaded")

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            // Initialize handlers with services
            InteractionHandler.init(Services.interactionScoreboard)
            ScoreboardHandler.init(Services.interactionScoreboard)

            // Register commands
            InteractionCommands.register(dispatcher)
            ScoreboardCommands.register(dispatcher)
        }
    }
}