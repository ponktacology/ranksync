package me.ponktacology.ranksync.command;

import me.ponktacology.ranksync.discord.listener.DiscordListener;
import me.ponktacology.ranksync.sync.SyncHelper;
import me.ponktacology.ranksync.util.ColorUtil;
import me.ponktacology.simpleconfig.config.annotation.Configurable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SyncCommand implements CommandExecutor {

    @Configurable
    public static String successTokenGenerationMessage = "&aGenerated synchronization token, go to #{channel} and type !sync {token} to synchronize your rank.";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!s.equalsIgnoreCase("sync")) return false;

        if(!(sender instanceof Player)) {
            sender.sendMessage(ColorUtil.color("&cYou need to be player to use this command."));
            return false;
        }

        Player player = (Player) sender;
        String token = SyncHelper.generateToken(player);

        player.sendMessage(ColorUtil.color(ColorUtil.color(successTokenGenerationMessage.replace("{channel}", DiscordListener.channelName).replace("{token}", token))));
        return true;
    }
}
