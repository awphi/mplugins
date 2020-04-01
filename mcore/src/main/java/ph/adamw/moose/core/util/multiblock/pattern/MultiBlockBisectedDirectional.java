package ph.adamw.moose.core.util.multiblock.pattern;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Stairs;
import ph.adamw.moose.core.util.LocationUtils;

public class MultiBlockBisectedDirectional extends MultiBlockBisected {
	private final BlockFace facing;

	public MultiBlockBisectedDirectional(Material material, int x, int y, int z, BlockFace facing, Bisected.Half half) {
		super(material, x, y, z, half);
		this.facing = facing;
	}

	@Override
	public boolean equals(Block block, int rotationsFromEast) {
		if(block.getBlockData() instanceof Directional) {
			final Directional data = (Directional) block.getBlockData();
			final BlockFace dir = LocationUtils.rotateFacing(facing, true, rotationsFromEast);
			return super.equals(block, rotationsFromEast) && dir.equals(data.getFacing());
		}

		return false;
	}
}
