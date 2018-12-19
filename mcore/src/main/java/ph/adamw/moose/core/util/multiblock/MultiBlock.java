package ph.adamw.moose.core.util.multiblock;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.config.AutoSerializable;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockPattern;

import java.util.UUID;

public abstract class MultiBlock extends AutoSerializable {
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

	@Getter
	@Setter
	private Plugin registeredPlugin;

	public abstract String getName();

	public abstract boolean isTrusted(Player player);

	public abstract void onCreate(Player player);

	public abstract void onActivate(PlayerInteractEvent player);

	public abstract void onDestroy();

	public void destroy() {
		onDestroy();
		MultiBlockHandler.delete(this);
	}
}
