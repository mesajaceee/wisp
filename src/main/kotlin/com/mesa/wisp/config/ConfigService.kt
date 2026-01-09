package com.mesa.wisp.config

import net.minecraft.text.Text

class ConfigService(
    private val repository: ConfigRepository
) {
    fun getMessage(
        key: MessageKeyTemplate,
        placeholders: Map<String, String> = emptyMap()
    ): String {
        val template = repository.getMessage(key.key)

        return placeholders.entries.fold(template) { acc, (ph, value) ->
            acc.replace("{$ph}", value)
        }
    }

    fun getMessageText(
        key: MessageKeyTemplate,
        placeholders: Map<String, String> = emptyMap()
    ): Text {
        val prefix = repository.getMessage(MessageKey.PREFIX.key)
        val message = getMessage(key, placeholders)
        val fullMessage = "$prefix $message"
        return Text.literal(translateColorCodes(fullMessage))
    }

    private fun translateColorCodes(text: String): String {
        return text.replace("&([0-9a-fk-or])".toRegex()) { matchResult ->
            "§${matchResult.groupValues[1]}"
        }
    }
}