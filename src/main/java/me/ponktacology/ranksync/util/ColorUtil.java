package me.ponktacology.ranksync.util;

import org.bukkit.ChatColor;

public class ColorUtil {
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
