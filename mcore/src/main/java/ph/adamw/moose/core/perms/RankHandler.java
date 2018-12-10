package ph.adamw.moose.core.perms;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class RankHandler {
	@Getter
	private static final Config config = new Config(MCore.getPlugin(), "ranks.yml");

	public static Rank getPrincipalRank(UUID uuid) {
		return Rank.getHighestPriority(getRanks(uuid));
	}

	public static String getFormattedName(OfflinePlayer player) {
		return getPrincipalRank(player.getUniqueId()).getColor() + player.getName();
	}

	public static List<Rank> getRanks(UUID uuid) {
		final List<Rank> ranks = new ArrayList<>();

		if(!(config.get(uuid.toString()) instanceof List)) {
			return ranks;
		}

		for(Object i : (List) config.get(uuid.toString())) {
			ranks.add(Rank.valueOf((String) i));
		}

		return ranks;
	}

	public static boolean hasPermission(CommandSender sender, Rank rank) {
		if(sender instanceof ConsoleCommandSender) {
			return true;
		} else if(sender instanceof Player) {
			return Rank.hasPermissions(getRanks(((Player) sender).getUniqueId()), rank);
		}

		return false;
	}
}
