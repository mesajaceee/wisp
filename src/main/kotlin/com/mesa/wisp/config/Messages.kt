package com.mesa.wisp.config

import com.mesa.wisp.interaction.Interaction

@JvmInline
value class MessageKeyTemplate(val key: String)

object MessageKey {
    // Interaction-specific keys
    fun interactionTargetSuccess(interaction: Interaction) =
        MessageKeyTemplate("messages.target.success.${interaction.name.lowercase()}")

    fun interactionSenderSuccess(interaction: Interaction) =
        MessageKeyTemplate("messages.sender.success.${interaction.name.lowercase()}")

    fun interactionSenderError(interaction: Interaction) =
        MessageKeyTemplate("messages.sender.error.${interaction.name.lowercase()}")

    fun interactionSelfError(interaction: Interaction) =
        MessageKeyTemplate("messages.sender.error.selfSocial.${interaction.name.lowercase()}")

    // Generic message keys
    val PREFIX = MessageKeyTemplate("messages.prefix")
    val SENDER_NOT_MEMBER = MessageKeyTemplate("messages.sender.error.senderNotMember")
    val TARGET_NOT_MEMBER = MessageKeyTemplate("messages.sender.error.targetNotMember")
    val TARGET_OFFLINE = MessageKeyTemplate("messages.sender.error.targetOffline")
    val TARGET_IGNORING_SENDER = MessageKeyTemplate("messages.sender.error.targetIgnoringSender")
    val SENDER_IGNORING_TARGET = MessageKeyTemplate("messages.sender.error.senderIgnoringTarget")
    val COOLDOWN = MessageKeyTemplate("messages.sender.error.cooldown")
    val SHAREHEALTH_NOT_ENOUGH = MessageKeyTemplate("messages.sender.error.sharehealth.notEnoughHealth")
    val SHAREHEALTH_TARGET_FULL = MessageKeyTemplate("messages.sender.error.sharehealth.targetFullHealth")
    val REJOIN_COOLDOWN = MessageKeyTemplate("messages.sender.error.rejoinCooldown")
    val REJOIN_ALREADY_MEMBER = MessageKeyTemplate("messages.sender.error.rejoinAlreadyMember")
    val IGNORE_ALREADY_IGNORING = MessageKeyTemplate("messages.sender.error.ignoreAlreadyIgnoring")
    val IGNORE_NOT_IGNORING = MessageKeyTemplate("messages.sender.error.ignoreNotIgnoring")

    // Scoreboard message keys
    val SCOREBOARD_PLAYER_NOT_FOUND = MessageKeyTemplate("messages.scoreboard.error.playerNotFound")
    val SCOREBOARD_UNKNOWN_INTERACTION = MessageKeyTemplate("messages.scoreboard.error.unknownInteraction")
    val SCOREBOARD_NO_DATA = MessageKeyTemplate("messages.scoreboard.noData")
    val SCOREBOARD_NO_INTERACTIONS = MessageKeyTemplate("messages.scoreboard.noInteractions")

    val SCOREBOARD_PLAYER_STATS_HEADER = MessageKeyTemplate("messages.scoreboard.playerStats.header")
    val SCOREBOARD_PLAYER_STATS_TOTAL = MessageKeyTemplate("messages.scoreboard.playerStats.total")
    val SCOREBOARD_PLAYER_STATS_BREAKDOWN = MessageKeyTemplate("messages.scoreboard.playerStats.breakdown")
    val SCOREBOARD_PLAYER_STATS_ITEM = MessageKeyTemplate("messages.scoreboard.playerStats.item")

    val SCOREBOARD_TOP_PLAYERS_HEADER = MessageKeyTemplate("messages.scoreboard.topPlayers.header")
    val SCOREBOARD_TOP_PLAYERS_ITEM = MessageKeyTemplate("messages.scoreboard.topPlayers.item")

    val SCOREBOARD_TOP_INTERACTION_HEADER = MessageKeyTemplate("messages.scoreboard.topInteraction.header")
    val SCOREBOARD_TOP_INTERACTION_ITEM = MessageKeyTemplate("messages.scoreboard.topInteraction.item")

    val SCOREBOARD_GLOBAL_STATS_HEADER = MessageKeyTemplate("messages.scoreboard.globalStats.header")
    val SCOREBOARD_GLOBAL_STATS_TOTAL = MessageKeyTemplate("messages.scoreboard.globalStats.total")
    val SCOREBOARD_GLOBAL_STATS_BREAKDOWN = MessageKeyTemplate("messages.scoreboard.globalStats.breakdown")
    val SCOREBOARD_GLOBAL_STATS_ITEM = MessageKeyTemplate("messages.scoreboard.globalStats.item")
}

object Messages {
    private val defaults = LinkedHashMap<MessageKeyTemplate, String>()

    private fun register(key: MessageKeyTemplate, value: String) {
        defaults[key] = value
    }

    fun defaults(): Map<String, String> = defaults.mapKeys { it.key.key }

    fun updateFromMap(map: Map<String, String>) {
        defaults.clear()
        map.forEach { (k, v) -> defaults[MessageKeyTemplate(k)] = v }
    }

    operator fun get(key: MessageKeyTemplate): String? = defaults[key]

    operator fun set(key: MessageKeyTemplate, value: String) = register(key, value)
}

object MessageDefinitions {
    init {
        // Prefix
        Messages[MessageKey.PREFIX] = "&7&o[&r&d&owisp&r&7&o]"

        // Generic error messages
        Messages[MessageKey.SENDER_NOT_MEMBER] = "&cI'm sorry, but you can't do that. You're not a member of the BeSocial program."
        Messages[MessageKey.TARGET_NOT_MEMBER] = "&cI'm sorry, but you can't do that. This player isn't a member of the BeSocial program."
        Messages[MessageKey.TARGET_OFFLINE] = "&cThis command can only be used if the targeted player is online."
        Messages[MessageKey.TARGET_IGNORING_SENDER] = "&cI'm afraid you can't do that."
        Messages[MessageKey.SENDER_IGNORING_TARGET] = "&cSorry, you can't interact with players you're ignoring."
        Messages[MessageKey.COOLDOWN] = "&cSorry, this command is currently cooling down. Please wait {time} seconds, then try again. &r&7&o(For help, try /besocial)"
        Messages[MessageKey.SHAREHEALTH_NOT_ENOUGH] = "&cSharing now would kill you."
        Messages[MessageKey.SHAREHEALTH_TARGET_FULL] = "&cThe chosen target already has full health."
        Messages[MessageKey.REJOIN_COOLDOWN] = "&cSorry, you can't rejoin yet. Please wait {time}, then try again."
        Messages[MessageKey.REJOIN_ALREADY_MEMBER] = "&cSorry, you can't rejoin, because you're already a member!"
        Messages[MessageKey.IGNORE_ALREADY_IGNORING] = "&cSorry, you're already ignoring that player."
        Messages[MessageKey.IGNORE_NOT_IGNORING] = "&cYou are currently not ignoring that player."

        // Scoreboard error messages
        Messages[MessageKey.SCOREBOARD_PLAYER_NOT_FOUND] = "&cPlayer not found"
        Messages[MessageKey.SCOREBOARD_UNKNOWN_INTERACTION] = "&cUnknown interaction: &4{interaction}"
        Messages[MessageKey.SCOREBOARD_NO_DATA] = "&7&oNo data yet!"
        Messages[MessageKey.SCOREBOARD_NO_INTERACTIONS] = "&7&oNo interactions yet!"

        // Scoreboard player stats
        Messages[MessageKey.SCOREBOARD_PLAYER_STATS_HEADER] = "&d&lStats for &5{player}&d&l"
        Messages[MessageKey.SCOREBOARD_PLAYER_STATS_TOTAL] = "&dTotal Interactions: &5{total}"
        Messages[MessageKey.SCOREBOARD_PLAYER_STATS_BREAKDOWN] = "&7Breakdown:"
        Messages[MessageKey.SCOREBOARD_PLAYER_STATS_ITEM] = "&f  {interaction}: &5{count}"

        // Scoreboard top players
        Messages[MessageKey.SCOREBOARD_TOP_PLAYERS_HEADER] = "&d&lTop {limit} Players (Total Interactions)"
        Messages[MessageKey.SCOREBOARD_TOP_PLAYERS_ITEM] = "&d{rank} &5{player}&d: &f{count}"

        // Scoreboard top interaction
        Messages[MessageKey.SCOREBOARD_TOP_INTERACTION_HEADER] = "&d&lTop {limit} Players ({interaction})"
        Messages[MessageKey.SCOREBOARD_TOP_INTERACTION_ITEM] = "&d{rank} &5{player}&d: &f{count}"

        // Scoreboard global stats
        Messages[MessageKey.SCOREBOARD_GLOBAL_STATS_HEADER] = "&d&lGlobal Interaction Statistics"
        Messages[MessageKey.SCOREBOARD_GLOBAL_STATS_TOTAL] = "&7Total Interactions: &f{total}"
        Messages[MessageKey.SCOREBOARD_GLOBAL_STATS_BREAKDOWN] = "&7Breakdown:"
        Messages[MessageKey.SCOREBOARD_GLOBAL_STATS_ITEM] = "&f  {interaction}: &5{count} &7({percentage})"

        // Sender success messages
        mapOf(
            Interaction.HUG to "&dYou hugged &5{target}&d!",
            Interaction.CUDDLE to "&dYou cuddled &5{target}&d!",
            Interaction.KISS to "&dYou kissed &5{target}&d!",
            Interaction.LICK to "&dYou licked &5{target}&d!",
            Interaction.POKE to "&dYou poked &5{target}&d!",
            Interaction.PET to "&dYou petted &5{target}&d!",
            Interaction.SLAP to "&4You slapped &c{target}&4!",
            Interaction.HIGHFIVE to "&dYou gave &5{target} &da high five!",
            Interaction.HANDSHAKE to "&dYou gave &5{target} &da handshake.",
            Interaction.SHAREHEALTH to "&dYou send &5{healthsend} &dhealth to &5{target}&d!"
        ).forEach { (interaction, message) ->
            Messages[MessageKey.interactionSenderSuccess(interaction)] = message
        }

        // Sender error messages
        mapOf(
            Interaction.HUG to "&cYou failed to hug &4{target}&c!",
            Interaction.CUDDLE to "&cYou failed to cuddle &4{target}&c!",
            Interaction.KISS to "&cYou failed to kiss &4{target}&c!",
            Interaction.LICK to "&cYou failed to lick &4{target}&c!",
            Interaction.POKE to "&cYou failed to poke &4{target}&c!",
            Interaction.PET to "&cYou failed to pet &4{target}&c!",
            Interaction.SLAP to "&cYou failed to slap &4{target}&c!",
            Interaction.HIGHFIVE to "&cYou failed to give &4{target} &ca high five!",
            Interaction.HANDSHAKE to "&cYou failed to give &4{target} &ca handshake.",
            Interaction.SHAREHEALTH to "&cYou failed to send health to &4{target}&c!"
        ).forEach { (interaction, message) ->
            Messages[MessageKey.interactionSenderError(interaction)] = message
        }

        // Target success messages
        mapOf(
            Interaction.HUG to "&5{sender} &dhugged you!",
            Interaction.CUDDLE to "&5{sender} &dcuddled you!",
            Interaction.KISS to "&5{sender} &dkissed you!",
            Interaction.LICK to "&5{sender} &dlicked you!",
            Interaction.POKE to "&5{sender} &dpoked you!",
            Interaction.PET to "&5{sender} &dpetted you!",
            Interaction.SLAP to "&c{sender} &4slapped you!",
            Interaction.HIGHFIVE to "&5{sender} &dgave you a high five!",
            Interaction.HANDSHAKE to "&5{sender} &dgives you a handshake.",
            Interaction.SHAREHEALTH to "&dYou received &5{healthsend} &dhealth from &5{sender}&d!"
        ).forEach { (interaction, message) ->
            Messages[MessageKey.interactionTargetSuccess(interaction)] = message
        }

        // Self-social error messages
        mapOf(
            Interaction.HUG to "&dYou hugged &5yourself &d! Now try hugging someone else.",
            Interaction.CUDDLE to "&dYou cuddled &5yourself &d! Now try hugging someone else.",
            Interaction.KISS to "&cQuite narcissistic, huh?",
            Interaction.LICK to "&dYou licked &5yourself&d! &r&o&7(That's disgusting...)",
            Interaction.POKE to "&dYou poked &5yourself&d!",
            Interaction.PET to "&dSo cute!",
            Interaction.SLAP to "&cDon't slap yourself! Everything is fine.",
            Interaction.HIGHFIVE to "&dWell done!",
            Interaction.HANDSHAKE to "&cBeing a politician today, huh?",
            Interaction.SHAREHEALTH to "&cThere is no point in doing this."
        ).forEach { (interaction, message) ->
            Messages[MessageKey.interactionSelfError(interaction)] = message
        }
    }
}