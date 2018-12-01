package ph.adamw.moose.rpg.brewing;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ph.adamw.moose.core.util.multiblock.MultiBlock;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockCore;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockElement;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockPattern;

public class MultiBlockVat extends MultiBlock {
	private final static transient MultiBlockPattern PATTERN = new MultiBlockPattern(
			// 2nd layer down
			new MultiBlockCore(Material.IRON_BARS),
			new MultiBlockElement(Material.IRON_BLOCK, 1, 0, 0),
			new MultiBlockElement(Material.IRON_BLOCK, 1, 0, 1),
			new MultiBlockElement(Material.IRON_BLOCK, 1, 0, -1),

			new MultiBlockElement(Material.IRON_BLOCK, -1, 0, 0),
			new MultiBlockElement(Material.IRON_BLOCK, -1, 0, 1),
			new MultiBlockElement(Material.IRON_BLOCK, -1, 0, -1),

			new MultiBlockElement(Material.IRON_BLOCK, 0, 0, 1),
			new MultiBlockElement(Material.IRON_BLOCK, 0, 0, -1),

			// 1st layer down
			new MultiBlockElement(Material.GLASS, 1, 1, 0),
			new MultiBlockElement(Material.IRON_BLOCK, 1, 1, 1),
			new MultiBlockElement(Material.IRON_BLOCK, 1, 1, -1),

			new MultiBlockElement(Material.GLASS, -1, 1, 0),
			new MultiBlockElement(Material.IRON_BLOCK, -1, 1, 1),
			new MultiBlockElement(Material.IRON_BLOCK, -1, 1, -1),

			new MultiBlockElement(Material.GLASS, 0, 1, 1),
			new MultiBlockElement(Material.GLASS, 0, 1, -1),
			new MultiBlockElement(Material.WATER, 0, 1, 0),

			// top layer
			new MultiBlockElement(Material.IRON_BLOCK, 1, 2, 0),
			new MultiBlockElement(Material.IRON_BLOCK, 1, 2, 1),
			new MultiBlockElement(Material.IRON_BLOCK, 1, 2, -1),

			new MultiBlockElement(Material.IRON_BLOCK, -1, 2, 0),
			new MultiBlockElement(Material.IRON_BLOCK, -1, 2, 1),
			new MultiBlockElement(Material.IRON_BLOCK, -1, 2, -1),

			new MultiBlockElement(Material.IRON_BLOCK, 0, 2, 1),
			new MultiBlockElement(Material.IRON_BLOCK, 0, 2, -1),

			// fire components
			new MultiBlockElement(Material.FIRE, 0 , -1, 0),
			new MultiBlockElement(Material.NETHERRACK, 0, -2, 0)
	);

	@Override
	public MultiBlockPattern getPattern() {
		return PATTERN;
	}

	@Override
	public String getName() {
		return "mixing vat";
	}

	@Override
	public boolean isTrusted(Player player) {
		return true;
	}

	@Override
	public void onCreate(Player player) {
		//TODO
	}

	@Override
	public void onActivate(PlayerInteractEvent player) {
		//TODO
	}

	@Override
	public void onDestroy() {
		//TODO
	}
}
