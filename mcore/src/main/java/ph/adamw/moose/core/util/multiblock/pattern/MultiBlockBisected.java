package ph.adamw.moose.core.util.multiblock.pattern;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;

public class MultiBlockBisected extends MultiBlockElement {
	protected final Bisected.Half half;

	public MultiBlockBisected(Material material, int x, int y, int z, Bisected.Half half) {
		super(material, x, y, z);
		this.half = half;
	}

	@Override
	public boolean equals(Block block, int rotationsFromEast) {
		if(block.getBlockData() instanceof Bisected) {
			final Bisected bisected = (Bisected) block.getBlockData();
			return super.equals(block, rotationsFromEast) && bisected.getHalf().equals(half);
		}

		return false;
	}
}
