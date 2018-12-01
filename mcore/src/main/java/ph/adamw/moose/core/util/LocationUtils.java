package ph.adamw.moose.core.util;

import org.bukkit.block.BlockFace;

public class LocationUtils {
	public static BlockFace rotateFacing(BlockFace up, boolean clockwise) {
		return rotateFacing(up, clockwise, 1);
	}

	public static BlockFace rotateFacing(BlockFace up, boolean clockwise, int rotations) {
		for(int i = 0; i < rotations; i ++) {
			final int next = (up.ordinal() + 4 + (clockwise ? 1 : -1)) % 4;
			up = BlockFace.values()[next];
		}

		return up;
	}
}
