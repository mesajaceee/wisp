package com.mesa.interaction

//import com.mesa.scoreboard.InteractionScoreboard
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.slf4j.LoggerFactory
import java.util.Locale
import java.util.Locale.getDefault

object InteractionHandler {
    private val logger = LoggerFactory.getLogger("wisp")
    fun handle(
        ctx: CommandContext<ServerCommandSource>,
        interaction: Interaction
    ): Int {
        val sender = ctx.source.player
        val target = EntityArgumentType.getPlayer(ctx, "target")

        val text = Text.literal(
            "[wisp] ${sender?.name?.string} ${interaction.verb} ${target.name.string}"
        )

        ctx.source.server.playerManager.broadcast(text, false)

        logger.info("${sender?.name?.string} used /${interaction.name.lowercase(getDefault())} on ${target.name.string}")

        //InteractionScoreboard.increment(sender, interaction)

        return 1
    }
}
