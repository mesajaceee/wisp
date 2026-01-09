package com.mesa.wisp

import com.mesa.wisp.scoreboard.ScoreboardService
import com.mesa.wisp.storage.JsonPersistenceManager
import com.mesa.wisp.scoreboard.ScoreboardRepositoryJson

object Services {
    private lateinit var jsonPersistenceManager: JsonPersistenceManager

    lateinit var interactionScoreboard: ScoreboardService
        private set

    fun init() {
        jsonPersistenceManager = JsonPersistenceManager("wisp/data.json")

        interactionScoreboard = ScoreboardService(
            ScoreboardRepositoryJson(jsonPersistenceManager)
        )

        println("Wisp services initialized")
    }
}