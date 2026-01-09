package com.mesa.wisp.config

import com.mesa.wisp.InternalConfig
import com.mesa.wisp.storage.JsonManager

class ConfigRepository(
    private val configManager: JsonManager
) {
    private val messages: MutableMap<String, String> by lazy {
        // Force MessageDefinitions to initialize before getting defaults
        MessageDefinitions

        val result = if (InternalConfig.OVERWRITE_CONFIG) {
            configManager.write("messages", Messages.defaults())
            Messages.defaults().toMutableMap()
        } else {
            configManager.read(
                namespace = "messages",
                clazz = Map::class.java,
                defaultValue = { Messages.defaults() }
            ).let { map ->
                map.entries.associate { it.key.toString() to it.value.toString() }.toMutableMap()
            }
        }

        result
    }

    fun getMessage(key: String): String {
        return messages[key] ?: key
    }
}