package com.mesa.wisp.config

import com.mesa.wisp.interaction.Interaction

data class ParticleConfig(
    val enableParticleEffect: Boolean = true,
    val particleAmount: Int = 10,
    val particleSpawnAreaSize: Double = 1.0,
    val onlyShowParticlesToParticipants: Boolean = false,
    val usedParticle: Map<String, String> = mapOf(
        Interaction.HUG.name.lowercase() to "heart",
        Interaction.CUDDLE.name.lowercase() to "heart",
        Interaction.KISS.name.lowercase() to "heart",
        Interaction.LICK.name.lowercase() to "happyVillager",
        Interaction.POKE.name.lowercase() to "composter",
        Interaction.PET.name.lowercase() to "heart",
        Interaction.SLAP.name.lowercase() to "angryVillager",
        Interaction.HIGHFIVE.name.lowercase() to "happyVillager",
        Interaction.HANDSHAKE.name.lowercase() to "happyVillager",
        Interaction.SHAREHEALTH.name.lowercase() to "heart"
    )
)

object ParticleConfigDefaults {
    fun defaults(): ParticleConfig = ParticleConfig()
}