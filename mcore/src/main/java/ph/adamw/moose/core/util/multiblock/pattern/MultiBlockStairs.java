package ph.adamw.moose.core.util.multiblock.pattern;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import ph.adamw.moose.core.util.LocationUtils;

public class MultiBlockStairs extends MultiBlockElement {
	private final Bisected.Half half;
	private final BlockFace facing;

	public MultiBlockStairs(Material material, int x, int y, int z, BlockFace facing, Bisected.Half half) {
		super(material, x, y, z);

		this.half = half;
		this.facing = facing;
	}

	@Override
	public boolean equals(Block block, int rotationsFromEast) {
		if(block.getBlockData() instanceof Stairs) {
			final Stairs stairs = (Stairs) block.getBlockData();
			final BlockFace dir = LocationUtils.rotateFacing(facing, true, rotationsFromEast);

			return super.equals(block, rotationsFromEast)
					&& stairs.getHalf().equals(half)
					&& dir.equals(stairs.getFacing());
		}

		return false;
	}
}
