package me.ponktacology.ranksync.discord;

import me.ponktacology.ranksync.discord.listener.DiscordListener;
import me.ponktacology.simpleconfig.config.annotation.Configurable;
import net.dv8tion.jda.api.JDABuilder;

public class DiscordBot {

    @Configurable(path = "discord.bot_token")
    public static String botToken = "";

    public DiscordBot() {
        JDABuilder builder = JDABuilder.createLight(botToken);
        builder.addEventListeners(new DiscordListener());
    }
}
