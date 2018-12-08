package ph.adamw.moose.core.perms;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import ph.adamw.moose.core.MCore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

public class RankListener implements Listener {
	private static final String MSG_FORMAT = "%1$s" + ChatColor.WHITE + " > " + ChatColor.GRAY + "%2$s";
	private static final Rank JOIN_RANK = Rank.MEMBER;

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event){
		if(!RankHandler.getConfig().contains(event.getPlayer().getUniqueId().toString())) {
			RankHandler.getConfig().set(event.getPlayer().getUniqueId().toString(), new ArrayList<>(Collections.singleton(JOIN_RANK.name())));
			MCore.getPlugin().getLogger().log(Level.INFO, "Applying rank " + JOIN_RANK.name() + " to " + event.getPlayer().getName());
		}
	}


	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event) {
		final ChatColor color = RankHandler.getPrincipalRank(event.getPlayer().getUniqueId()).getColor();
		event.setFormat(color + MSG_FORMAT);
	}
}
