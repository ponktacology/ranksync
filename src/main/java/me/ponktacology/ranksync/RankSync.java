package me.ponktacology.ranksync;

import me.ponktacology.ranksync.command.SyncCommand;
import me.ponktacology.ranksync.discord.DiscordBot;
import org.bukkit.plugin.java.JavaPlugin;

public final class RankSync extends JavaPlugin {

    @Override
    public void onEnable() {
        new DiscordBot();
        getCommand("sync").setExecutor(new SyncCommand());
    }
}
