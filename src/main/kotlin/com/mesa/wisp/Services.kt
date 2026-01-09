package com.mesa.wisp

import com.mesa.wisp.service.InteractionScoreboardService
import com.mesa.wisp.storage.JsonPersistenceManager
import com.mesa.wisp.repository.InteractionScoreboardRepositoryJson

object Services {
    private lateinit var jsonPersistenceManager: JsonPersistenceManager

    lateinit var interactionScoreboard: InteractionScoreboardService
        private set

    fun init() {
        jsonPersistenceManager = JsonPersistenceManager("wisp/data.json")

        interactionScoreboard = InteractionScoreboardService(
            InteractionScoreboardRepositoryJson(jsonPersistenceManager)
        )

        println("Wisp services initialized")
    }
}