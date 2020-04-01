package ph.adamw.moose.rpg.state;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ph.adamw.moose.core.util.config.Config;
import ph.adamw.moose.rpg.MRpg;

import java.util.logging.Level;

public class RpgPlayerRegistry implements Listener {
	private static final Config config = new Config(MRpg.getPlugin(), "playerstates.yml");

	private static void register(OfflinePlayer player) {
		config.set(player.getUniqueId().toString(), new RpgPlayer(player));
	}

	private static boolean isRegistered(OfflinePlayer player) {
		return config.contains(player.getUniqueId().toString());
	}

	static RpgPlayer getRpgPlayer(OfflinePlayer player) {
		return (RpgPlayer) config.get(player.getUniqueId().toString());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event){
		if(!isRegistered(event.getPlayer())) {
			MRpg.getPlugin().getLogger().log(Level.INFO, "Creating new RpgPlayer for new player: " + event.getPlayer().getName());
			register(event.getPlayer());
		}
	}
}
