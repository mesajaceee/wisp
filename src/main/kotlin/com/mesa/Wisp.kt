package com.mesa

import com.mesa.interaction.InteractionCommands
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import org.slf4j.LoggerFactory

object Wisp : ModInitializer {
    private val logger = LoggerFactory.getLogger("wisp")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("wisp is loaded")

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            InteractionCommands.register(dispatcher)
        }
	}
}