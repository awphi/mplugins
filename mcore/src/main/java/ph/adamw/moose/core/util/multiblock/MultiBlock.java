package ph.adamw.moose.core.util.multiblock;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ph.adamw.moose.core.util.config.AutoSerializable;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockPattern;

import java.util.UUID;


public abstract class MultiBlock extends AutoSerializable {
	@Getter
	@Setter
	private UUID owner;

	@Getter
	@Setter
	private Location coreLocation;

	public abstract MultiBlockPattern getPattern();

	public abstract String getName();

	public abstract boolean isTrusted(Player player);

	public abstract void onCreate(Player player);

	public abstract void onActivate(Player player);

	public abstract void onDestroy(Player player);
}
