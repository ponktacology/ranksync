package me.ponktacology.ranksync.discord.listener;

import com.mizuledevelopment.chronium.Chronium;
import com.mizuledevelopment.chronium.profile.Profile;
import me.ponktacology.ranksync.sync.SyncHelper;
import me.ponktacology.simpleconfig.config.annotation.Configurable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DiscordListener extends ListenerAdapter {

    @Configurable(path = "discord.sync")
    public static String channelName = "sync";

    @Configurable(path = "discord.message.invalid_format")
    public static String invalidFormatMessage = "Invalid command format, !sync <token>";

    @Configurable(path = "discord.message.incorrect_token")
    public static String incorrectTokenMessage = "Incorrect token";

    @Configurable(path = "discord.message.player_must_be_online")
    public static String mustBeOnlineMessage = "You must be online in order to synchronize";

    @Configurable(path = "discord.message.successfully_synchronized")
    public static String syncSuccessMessage = "Successfully synchronized your rank";

    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getChannel().getName().equals(channelName)) return;
        String message = event.getMessage().getContentDisplay();
        if (!message.startsWith("!sync")) return;
        event.getMessage().delete();

        String token = "";

        try {
            token = message.split(" ")[1];
        } catch (Exception e) {
            event.getChannel().sendMessage(invalidFormatMessage)
                    .mentionUsers(event.getAuthor().getId())
                    .delay(30, TimeUnit.SECONDS, service)
                    .flatMap(Message::delete);
            return;
        }

        if (!SyncHelper.isTokenCorrect(token)) {
            event.getChannel().sendMessage(incorrectTokenMessage)
                    .mentionUsers(event.getAuthor().getId())
                    .delay(30, TimeUnit.SECONDS, service)
                    .flatMap(Message::delete);
            return;
        }

        Player player = SyncHelper.getPlayerFromToken(token);

        if (player == null) {
            event.getChannel().sendMessage(mustBeOnlineMessage)
                    .mentionUsers(event.getAuthor().getId())
                    .delay(30, TimeUnit.SECONDS, service)
                    .flatMap(Message::delete);
            return;
        }

        Profile profile = Chronium.getInstance().getProfileHandler().getProfileFromUUID(player.getUniqueId());

        if (profile == null) {
            event.getChannel().sendMessage(mustBeOnlineMessage)
                    .mentionUsers(event.getAuthor().getId())
                    .delay(30, TimeUnit.SECONDS, service)
                    .flatMap(Message::delete);
            return;
        }

        Role role = SyncHelper.convertRankToRole(event.getGuild(), profile.getActiveRank());

        removeAllRankRoles(event.getGuild(), event.getAuthor());
        event.getGuild().addRoleToMember(event.getAuthor().getId(), role);

        event.getMessage().delete();
        event.getChannel().sendMessage(syncSuccessMessage)
                .mentionUsers(event.getAuthor().getId())
                .delay(30, TimeUnit.SECONDS, service)
                .flatMap(Message::delete);
        SyncHelper.invalidateCache(token);
    }


    public void removeAllRankRoles(Guild guild, User user) {
        List<Role> roles = Chronium.getInstance().getRankHandler().getSortedRanks().stream().map(it -> SyncHelper.convertRankToRole(guild, it)).filter(Objects::nonNull).collect(Collectors.toList());

        roles.forEach(role -> {
            guild.removeRoleFromMember(user.getId(), role);
        });
    }
}
