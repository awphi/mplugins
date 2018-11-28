package ph.adamw.moose.core.perms;

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

public class RankHandler implements Listener {
	private static final String MSG_FORMAT = "%1$s" + ChatColor.WHITE + " > " + ChatColor.GRAY + "%2$s";
	private static final Rank JOIN_RANK = Rank.MEMBER;

	private final Config config = MCore.getPlugin().getRankConfig();

	public RankHandler() {
		MCore.getPlugin().getServer().getPluginManager().registerEvents(this, MCore.getPlugin());
	}

	public Rank getPrincipalRank(UUID uuid) {
		return Rank.getHighestPriority(getRanks(uuid));
	}

	public String getFormattedName(OfflinePlayer player) {
		return getPrincipalRank(player.getUniqueId()).getColor() + player.getName();
	}

	public List<Rank> getRanks(UUID uuid) {
		final List<Rank> ranks = new ArrayList<>();

		if(!(config.get(uuid.toString()) instanceof List)) {
			return ranks;
		}

		for(Object i : (List) config.get(uuid.toString())) {
			ranks.add(Rank.valueOf((String) i));
		}

		return ranks;
	}

	public boolean hasPermission(CommandSender sender, Rank rank) {
		if(sender instanceof ConsoleCommandSender) {
			return true;
		} else if(sender instanceof Player) {
			return Rank.hasPermissions(getRanks(((Player) sender).getUniqueId()), rank);
		}

		return false;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event){
		if(!config.contains(event.getPlayer().getUniqueId().toString())) {
			config.set(event.getPlayer().getUniqueId().toString(), new ArrayList<>(Collections.singleton(JOIN_RANK.name())));
			MCore.getPlugin().getLogger().log(Level.INFO, "Applying rank " + JOIN_RANK.name() + " to " + event.getPlayer().getName());
		}
	}


	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event) {
		final ChatColor color = getPrincipalRank(event.getPlayer().getUniqueId()).getColor();
		event.setFormat(color + MSG_FORMAT);
	}
}
