package ph.adamw.moose.eco;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ph.adamw.moose.core.util.config.Config;

import java.util.UUID;
import java.util.logging.Level;

public class EconomyHandler implements Listener {
	private final Config config = MEconomy.getPlugin().getEcoConfig();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		if(!config.contains(event.getPlayer().getUniqueId().toString())) {
			config.set(event.getPlayer().getUniqueId().toString(), new EconomyProfile());
			MEconomy.getPlugin().getLogger().log(Level.INFO, "Creating new economic profile for " + event.getPlayer().getName());
		}
	}

	public boolean attemptTransfer(Player sender, OfflinePlayer receiver, double amount) {
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
		return (EconomyProfile) config.get(uniqueId.toString());
	}
}
