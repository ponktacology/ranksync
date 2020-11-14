package me.ponktacology.ranksync.sync;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mizuledevelopment.chronium.rank.Rank;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SyncHelper {

    private static final Cache<String, UUID> tokenCache = CacheBuilder.newBuilder().expireAfterWrite(60 * 5, TimeUnit.SECONDS).build();

    public static void invalidateCache(String token) {
        tokenCache.invalidate(token);
    }

    public static String generateToken(Player player) {
        String token = String.valueOf(Math.random() * 10000);
        tokenCache.put(token, player.getUniqueId());

        return token;
    }

    public static boolean isTokenCorrect(String token) {
        return tokenCache.asMap().containsKey(token);
    }

    public static Player getPlayerFromToken(String token) {
        UUID uuid = tokenCache.getIfPresent(token);

        if (uuid == null) return null;

        return Bukkit.getPlayer(uuid);
    }

    public static Role convertRankToRole(Guild guild, Rank rank) {
        return guild.getRoles().stream().filter(it -> it.getName().equalsIgnoreCase(rank.getName())).findFirst().orElse(null);
    }

}
