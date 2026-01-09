package com.mesa.wisp.particle

import com.mesa.wisp.config.ParticleConfig
import com.mesa.wisp.interaction.Interaction
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d
import org.slf4j.LoggerFactory

class ParticleSpawner(
    private val config: ParticleConfig
) {
    private val logger = LoggerFactory.getLogger("wisp-particles")

    /**
     * Spawn particles around players during an interaction
     */
    fun spawnParticlesInPlayer(
        sender: ServerPlayerEntity,
        target: ServerPlayerEntity,
        interaction: Interaction
    ) {
        if (!config.enableParticleEffect) {
            return
        }

        val particle = getParticleForInteraction(interaction)
        val amount = config.particleAmount
        val areaModifier = config.particleSpawnAreaSize
        val deltaX = 0.3 * areaModifier
        val deltaY = 0.4 * areaModifier
        val deltaZ = 0.3 * areaModifier

        // Same player interacting with themselves
        if (sender.uuid == target.uuid) {
            val location = getPlayerHeartLocation(sender)
            val world = sender.entityWorld as ServerWorld
            if (config.onlyShowParticlesToParticipants) {
                spawnForPlayer(world, sender, particle, location, amount, deltaX, deltaY, deltaZ)
            } else {
                spawnInWorld(world, particle, location, amount, deltaX, deltaY, deltaZ)
            }
            return
        }

        // Two different players
        val senderLocation = getPlayerHeartLocation(sender)
        val targetLocation = getPlayerHeartLocation(target)
        val senderWorld = sender.entityWorld as ServerWorld
        val targetWorld = target.entityWorld as ServerWorld

        if (config.onlyShowParticlesToParticipants) {
            // Only sender and target see the particles
            spawnForPlayer(senderWorld, sender, particle, senderLocation, amount, deltaX, deltaY, deltaZ)
            spawnForPlayer(senderWorld, sender, particle, targetLocation, amount, deltaX, deltaY, deltaZ)
            spawnForPlayer(targetWorld, target, particle, senderLocation, amount, deltaX, deltaY, deltaZ)
            spawnForPlayer(targetWorld, target, particle, targetLocation, amount, deltaX, deltaY, deltaZ)
        } else {
            // Everyone nearby sees the particles
            spawnInWorld(senderWorld, particle, senderLocation, amount, deltaX, deltaY, deltaZ)
            if (senderWorld == targetWorld) {
                spawnInWorld(targetWorld, particle, targetLocation, amount, deltaX, deltaY, deltaZ)
            }
        }
    }

    /**
     * Get particle spawn location at player's chest/heart level
     */
    private fun getPlayerHeartLocation(player: ServerPlayerEntity): Vec3d {
        return player.entityPos.add(0.0, 1.5, 0.0)
    }

    /**
     * Spawn particles visible only to a specific player
     *
     * Uses ServerWorld.spawnParticles with the player parameter
     */
    private fun spawnForPlayer(
        world: ServerWorld,
        player: ServerPlayerEntity,
        particle: ParticleEffect,
        location: Vec3d,
        count: Int,
        deltaX: Double,
        deltaY: Double,
        deltaZ: Double
    ) {
        // This method sends particles only to the specified player
		//T parameters, boolean force, boolean important, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double speed
        world.spawnParticles(
            particle,         // Particle type
            true,             // Force spawn (ignore player's particle settings)
            true,
            location.x,       // X position
            location.y,       // Y position
            location.z,       // Z position
            count,            // Number of particles
            deltaX,           // X spread/offset
            deltaY,           // Y spread/offset
            deltaZ,           // Z spread/offset
            0.0               // Particle speed
        )
    }

    /**
     * Spawn particles visible to all players in the world
     *
     * Uses ServerWorld.spawnParticles without player parameter
     */
    private fun spawnInWorld(
        world: ServerWorld,
        particle: ParticleEffect,
        location: Vec3d,
        count: Int,
        deltaX: Double,
        deltaY: Double,
        deltaZ: Double
    ) {
        // This method sends particles to all players in range
        world.spawnParticles(
            particle,         // Particle type
            location.x,       // X position
            location.y,       // Y position
            location.z,       // Z position
            count,            // Number of particles
            deltaX,           // X spread/offset
            deltaY,           // Y spread/offset
            deltaZ,           // Z spread/offset
            0.0               // Particle speed
        )
    }

    /**
     * Get the particle type for a specific interaction
     */
    private fun getParticleForInteraction(interaction: Interaction): ParticleEffect {
        val particleName = config.usedParticle[interaction.name.lowercase()] ?: "heart"
        return stringToParticle(particleName)
    }

    /**
     * Convert config string to Minecraft ParticleEffect
     */
    private fun stringToParticle(particleName: String): ParticleEffect {
        return when (particleName.lowercase()) {
            "heart", "hearts" -> ParticleTypes.HEART
            "composter" -> ParticleTypes.COMPOSTER
            "happyvillager" -> ParticleTypes.HAPPY_VILLAGER
            "fallingwater" -> ParticleTypes.FALLING_WATER
            "angryvillager" -> ParticleTypes.ANGRY_VILLAGER
            "flame" -> ParticleTypes.FLAME
            "smoke" -> ParticleTypes.SMOKE
            "sparkle" -> ParticleTypes.END_ROD
            "enchant" -> ParticleTypes.ENCHANT
            "cloud" -> ParticleTypes.CLOUD
            "note" -> ParticleTypes.NOTE
            "portal" -> ParticleTypes.PORTAL
            "splash" -> ParticleTypes.SPLASH
            "snowflake" -> ParticleTypes.SNOWFLAKE
            else -> {
                logger.warn("Unknown particle type '$particleName', using default 'heart'")
                ParticleTypes.HEART
            }
        }
    }
}