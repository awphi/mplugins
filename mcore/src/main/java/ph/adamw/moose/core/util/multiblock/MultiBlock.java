package ph.adamw.moose.core.util.multiblock;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.config.AutoSerializable;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockPattern;

import java.util.UUID;

public abstract class MultiBlock extends AutoSerializable implements Listener {
	public MultiBlock() {
		MCore.getPlugin().getServer().getPluginManager().registerEvents(this, MCore.getPlugin());
	}

	private String ownerUuid;

	public void setOwner(OfflinePlayer player) {
		ownerUuid = player.getUniqueId().toString();
	}

	public OfflinePlayer getOwner() {
		return Bukkit.getOfflinePlayer(UUID.fromString(ownerUuid));
	}

	@Getter
	@Setter
	private Location coreLocation;

	public abstract MultiBlockPattern getPattern();

	public abstract String getName();

	public abstract boolean isTrusted(Player player);

	public abstract void onCreate(Player player);

	public abstract void onActivate(PlayerInteractEvent player);

	public abstract void onDestroy();

	public void destroy() {
		onDestroy();

		// Prune the configs of this multiblock
		for(Location i : getPattern().traversePattern(getCoreLocation())) {
			MultiBlockHandler.getProtectionSection().set(i.toString(), null);
		}

		MultiBlockHandler.getMultiblocksSection().set(getCoreLocation().toString(), null);
	}
}
