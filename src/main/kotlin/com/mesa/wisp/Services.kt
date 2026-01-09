package com.mesa.wisp

import com.mesa.wisp.config.ConfigRepository
import com.mesa.wisp.config.ConfigService
import com.mesa.wisp.config.ParticleConfig
import com.mesa.wisp.particle.ParticleSpawner
import com.mesa.wisp.scoreboard.ScoreboardService
import com.mesa.wisp.storage.JsonManager
import com.mesa.wisp.scoreboard.ScoreboardRepositoryJson

object Services {
    private lateinit var jsonDataManager: JsonManager
    private lateinit var jsonConfigManager: JsonManager

    lateinit var scoreboardService: ScoreboardService
        private set

    lateinit var configService: ConfigService
        private set

    lateinit var particleSpawner: ParticleSpawner
        private set

    fun init() {
        jsonConfigManager = JsonManager("wisp/config.json")
        jsonDataManager = JsonManager("wisp/data.json")

        particleSpawner = ParticleSpawner(
            ParticleConfig()
        )

        configService = ConfigService(
            ConfigRepository(jsonConfigManager)
        )

        scoreboardService = ScoreboardService(
            ScoreboardRepositoryJson(jsonDataManager)
        )

        println("Wisp services initialized")
    }
}