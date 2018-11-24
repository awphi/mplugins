package ph.adamw.moose.eco;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ph.adamw.moose.core.util.PluginFile;

import java.util.UUID;

public class EconomyHandler implements Listener {
	private static final String PREFIX = "account_";
	private final PluginFile config = MEconomy.getPlugin().getEcoConfig();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		if(!config.contains(PREFIX + event.getPlayer().getUniqueId())) {
			config.set(PREFIX + event.getPlayer().getUniqueId(), new EconomyProfile());
			System.out.println("Creating new eco profile for " + event.getPlayer().getName());
		}
	}

	public boolean attemptTransfer(Player sender, OfflinePlayer receiver, int amount) {
		final EconomyProfile from = getProfile(sender.getUniqueId());
		final EconomyProfile to = getProfile(receiver.getUniqueId());

		if(amount > from.getBalance()) {
			return false;
		}

		from.removeBalance(amount);
		to.addBalance(amount);
		return true;
	}

	public EconomyProfile getProfile(UUID uniqueId) {
		return (EconomyProfile) config.get(PREFIX + uniqueId);
	}
}
